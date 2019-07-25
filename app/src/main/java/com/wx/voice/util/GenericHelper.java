/*
 * Copyright 2016 XuJiaji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wx.voice.util;

import com.wx.voice.base.contracts.XContract;
import com.wx.voice.base.presenters.XBasePresenter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class GenericHelper {

    public static <T> Class<T> getGenericClass(Class<?> klass, Class<?> filterClass) {
        Type type = klass.getGenericSuperclass();
        if (type == null || !(type instanceof ParameterizedType)) return null;
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        for (Type t : types) {
            Class<T> tClass = (Class<T>) t;
            if (filterClass.isAssignableFrom(tClass)) {
                return tClass;
            }
        }

        return null;
//        if(types == null || types.length == 0) return null;
//        return (Class<T>) types[0];
    }

    private static Class<?> getViewClass(Class<?> klass, Class<?> filterClass) {
        for (Class c : klass.getInterfaces()) {
            if (filterClass.isAssignableFrom(c)) return klass;
        }
        return getViewClass(klass.getSuperclass(), filterClass);
    }


    public static <T> T newPresenter(Object obj) {
        if (!XContract.View.class.isInstance(obj)) {
            throw new RuntimeException("no implement XContract.BaseView");
        }
        try {
            Class<?> currentClass = obj.getClass();
            Class<?> viewClass = getViewClass(currentClass, XContract.View.class);
            Class<?> presenterClass = getGenericClass(viewClass, XContract.Presenter.class);
            Class<?> modelClass = getGenericClass(presenterClass, XContract.Model.class);
//            Constructor construct = presenterClass.getConstructor(viewClass, modelClass);
            XBasePresenter<?, ?> xBasePresenter = (XBasePresenter<?, ?>) presenterClass.newInstance();
            xBasePresenter.init(obj, modelClass != null ? modelClass.newInstance() : null);
            return (T) xBasePresenter;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("instance of com.presenter fail\n" +
                " Remind com.presenter need to extends XBasePresenter");
    }

}