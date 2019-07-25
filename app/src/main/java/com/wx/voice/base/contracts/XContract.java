/*
 * Copyright 2016 XuJiaji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wx.voice.base.contracts;

import com.wx.voice.respones.MusicTypeRespones; /**
 * MVP基础接口 <br />
 * MVP base interface
 */
public interface XContract {
    interface Presenter {

    }

    interface View {

        void showLoading();

        void showError(int type, String msg);


    }

    interface Model {

    }
}
