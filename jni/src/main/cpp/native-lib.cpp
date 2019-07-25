#include <jni.h>
#include <string>
#include <android/log.h>

#define TAG "wyz" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

const char HexCode[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
                        'F'};


extern "C"
__attribute__((section (".mytext")))
JNIEXPORT jobject JNICALL
ld(JNIEnv *env, jobject thisObj, jobject context, jstring jarPath, jobject file, jobject list) {
    jclass context_class = env->GetObjectClass(context);
    //context.getPackageManager()
    jmethodID methodId = env->GetMethodID(context_class, "getPackageManager",
                                          "()Landroid/content/pm/PackageManager;");
    jobject package_manager_object = env->CallObjectMethod(context, methodId);
    if (package_manager_object == NULL) {
        return env->NewStringUTF("");
    }

    //context.getPackageName()
    methodId = env->GetMethodID(context_class, "getPackageName", "()Ljava/lang/String;");
    jstring package_name_string = env->NewStringUTF("com.wx.voice");
    if (package_name_string == NULL) {
        return env->NewStringUTF("");
    }
    env->DeleteLocalRef(context_class);

    //PackageManager.getPackageInfo(Sting, int)
    //public static final int GET_SIGNATURES= 0x00000040;
    jclass pack_manager_class = env->GetObjectClass(package_manager_object);
    methodId = env->GetMethodID(pack_manager_class, "getPackageInfo",
                                "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    env->DeleteLocalRef(pack_manager_class);
    jobject package_info_object = env->CallObjectMethod(package_manager_object, methodId,
                                                        package_name_string, 0x40);
    if (package_info_object == NULL) {
        return env->NewStringUTF("");
    }
    env->DeleteLocalRef(package_manager_object);

    //PackageInfo.signatures[0]
    jclass package_info_class = env->GetObjectClass(package_info_object);
    jfieldID fieldId = env->GetFieldID(package_info_class, "signatures",
                                       "[Landroid/content/pm/Signature;");
    env->DeleteLocalRef(package_info_class);
    jobjectArray signature_object_array = (jobjectArray) env->GetObjectField(package_info_object,
                                                                             fieldId);
    if (signature_object_array == NULL) {
        return env->NewStringUTF("");
    }
    jobject signature_object = env->GetObjectArrayElement(signature_object_array, 0);
    env->DeleteLocalRef(package_info_object);

    //Signature.toByteArray()
    jclass signature_class = env->GetObjectClass(signature_object);
    methodId = env->GetMethodID(signature_class, "toByteArray", "()[B");
    env->DeleteLocalRef(signature_class);
    jbyteArray signature_byte = (jbyteArray) env->CallObjectMethod(signature_object, methodId);


    //new ByteArrayInputStream
    jclass byte_array_input_class = env->FindClass("java/io/ByteArrayInputStream");
    methodId = env->GetMethodID(byte_array_input_class, "<init>", "([B)V");
    jobject byte_array_input = env->NewObject(byte_array_input_class, methodId, signature_byte);
    env->DeleteLocalRef(byte_array_input_class);
    //CertificateFactory.getInstance("X.509")
    jclass certificate_factory_class = env->FindClass("java/security/cert/CertificateFactory");
    methodId = env->GetStaticMethodID(certificate_factory_class, "getInstance",
                                      "(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;");
    jstring x_509_jstring = env->NewStringUTF("X.509");
    jobject cert_factory = env->CallStaticObjectMethod(certificate_factory_class, methodId,
                                                       x_509_jstring);

    //certFactory.generateCertificate(byteIn);
    methodId = env->GetMethodID(certificate_factory_class, "generateCertificate",
                                ("(Ljava/io/InputStream;)Ljava/security/cert/Certificate;"));
    jobject x509_cert = env->CallObjectMethod(cert_factory, methodId, byte_array_input);
    env->DeleteLocalRef(certificate_factory_class);
    //cert.getEncoded()
    jclass x509_cert_class = env->GetObjectClass(x509_cert);
    methodId = env->GetMethodID(x509_cert_class, "getEncoded", "()[B");
    jbyteArray cert_byte = (jbyteArray) env->CallObjectMethod(x509_cert, methodId);
    env->DeleteLocalRef(x509_cert_class);
    //MessageDigest.getInstance("SHA1")
    jclass message_digest_class = env->FindClass("java/security/MessageDigest");
    methodId = env->GetStaticMethodID(message_digest_class, "getInstance",
                                      "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jstring sha1_jstring = env->NewStringUTF("SHA1");
    jobject sha1_digest = env->CallStaticObjectMethod(message_digest_class, methodId, sha1_jstring);

    //sha1.digest (certByte)
    methodId = env->GetMethodID(message_digest_class, "digest", "([B)[B");
    jbyteArray sha1_byte = (jbyteArray) env->CallObjectMethod(sha1_digest, methodId, cert_byte);
    env->DeleteLocalRef(message_digest_class);

    //toHexString
    jsize array_size = env->GetArrayLength(sha1_byte);
    jbyte *sha1 = env->GetByteArrayElements(sha1_byte, NULL);
    char *hex_sha = new char[array_size * 2 + 1];
    for (int i = 0; i < array_size; ++i) {
        hex_sha[2 * i] = HexCode[((unsigned char) sha1[i]) / 16];
        hex_sha[2 * i + 1] = HexCode[((unsigned char) sha1[i]) % 16];
    }
    hex_sha[array_size * 2] = '\0';
    //比较签名
    if (strcmp(hex_sha, "D254BB6952282B003C6AE555F021097DE43AE67C") == 0) {
        jobject obj = NULL;
        jclass contextClass = env->GetObjectClass(context);
        //1:        DesUtil desUtil = new DesUtil();
        jclass desClzz = env->FindClass("com/wx/voice/util/Dd");
        jmethodID desC = env->GetMethodID(desClzz, "<init>", "(Landroid/content/Context;)V");
        jobject desObj = env->NewObject(desClzz, desC, context);
        //2:        desUtil.dd(jarPath, decryptDexPath);
        jmethodID doDecryptFileMid = env->GetMethodID(desClzz, "dd",
                                                      "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
        jstring pJobject = (jstring) env->CallObjectMethod(desObj, doDecryptFileMid, jarPath,
                                                           env->NewStringUTF("bat.dex"));

        jclass dxclass = env->FindClass("dalvik/system/BaseDexClassLoader");
        if (dxclass != NULL) {
            jmethodID dxMethod = env->GetMethodID(dxclass, "<init>",
                                                  "(Ljava/lang/String;Ljava/io/File;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
            if (dxMethod != NULL) {
                jmethodID classMethod = env->GetMethodID(contextClass, "getClassLoader",
                                                         "()Ljava/lang/ClassLoader;");
                jobject classObj = env->CallObjectMethod(context, classMethod);
                jobject dex = env->NewObject(dxclass, dxMethod, pJobject, file, NULL, classObj);
                
//                // 注入
//                jclass mmCoreClzz = env->GetObjectClass(thisObj);
//
//                jmethodID injectMethod = env->GetStaticMethodID(mmCoreClzz, "inject",
//                                                         "(Ldalvik/system/BaseDexClassLoader;Landroid/content/Context;)V");
//                env->CallStaticVoidMethod(mmCoreClzz, injectMethod, dex, context);
                
                jmethodID pID = env->GetMethodID(dxclass, "loadClass",
                                                 "(Ljava/lang/String;)Ljava/lang/Class;");
                jstring pJstring = env->NewStringUTF("com.nine.remotemm.JarObject");
                jclass cclzz = (jclass) env->CallObjectMethod(dex, pID, pJstring);
                jmethodID corMethod = env->GetMethodID(cclzz, "<init>", "()V");
                obj = env->NewObject(cclzz, corMethod);
                env->DeleteLocalRef(cclzz);
            }
        }
        const char *string = env->GetStringUTFChars((jstring) pJobject, NULL);
        const char *string2 = env->GetStringUTFChars((jstring) jarPath, NULL);
        remove(string);
        remove(string2);

        //删除路径
        char str[80];
        strcpy(str, string);
        int L, i;
        L = strlen(string);
        for (i = L - 1; i >= 0; i--)
            if (str[i] == '/') {
                str[i] = '\0';
                break;
            };
        remove(str);

        strcpy(str, string2);
        L = strlen(string2);
        for (i = L - 1; i >= 0; i--)
            if (str[i] == '/') {
                str[i] = '\0';
                break;
            };
        remove(str);

        //删除cache/bat.dex
        L = strlen(str);
        for (i = L - 1; i >= 0; i--)
            if (str[i] == '/') {
                str[i] = '\0';
                break;
            };
        char data[20] = "/cache/bat.dex";
        strcat(str, data);
        remove(str);

        //删除cache目录
        L = strlen(str);
        for (i = L - 1; i >= 0; i--)
            if (str[i] == '/') {
                str[i] = '\0';
                break;
            };
        remove(str);
        env->DeleteLocalRef(desClzz);
        env->DeleteLocalRef(dxclass);
        env->DeleteLocalRef(contextClass);
        env->ReleaseStringUTFChars((jstring) pJobject, string);
        env->ReleaseStringUTFChars((jstring) jarPath, string2);
        return obj;
    }
    env->DeleteLocalRef(x_509_jstring);
    env->DeleteLocalRef(sha1_jstring);
}

__attribute__((section (".mytext")))
JNIEXPORT void JNICALL rep//替换密码
        (JNIEnv *env, jobject jobj) {
    jclass clz = env->GetObjectClass(jobj);
    jfieldID pID = env->GetFieldID(clz, "CORE", "Ljava/lang/String;");
    jstring jstr = (jstring) env->GetObjectField(jobj, pID);
    const char *cstr = env->GetStringUTFChars(jstr, NULL);
    char *res = "AABBCCDDEE112233";
    if (strcmp(res, cstr) == 0) {
        char *pasRes = "RESUL_ADD_PASS";
        jstring pJstring = env->NewStringUTF(pasRes);
        env->SetObjectField(jobj, pID, pJstring);
        env->DeleteLocalRef(pJstring);
    }
    env->DeleteLocalRef(clz);
    env->ReleaseStringUTFChars(jstr, cstr);
}



// 获取数组的大小
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#define JNIREG_CLASS "com/wx/voice/core/MMCore"
// Java和JNI函数的绑定表
static JNINativeMethod method_table[] = {
        {"load", "(Landroid/content/Context;Ljava/lang/String;Ljava/io/File;Ljava/util/List;)Ljava/lang/Object;", (void *) ld},
};

//绑定，注意，V,Z签名的返回值不能有分号“;”
static JNINativeMethod Dd[] = {
        {"init", "()V", (void *) rep},
};


// 注册native方法到java中
static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

int register_ndk_load(JNIEnv *env) {
    // 调用注册方法
    registerNativeMethods(env, JNIREG_CLASS,
                          method_table, NELEM(method_table));

    registerNativeMethods(env, "com/wx/voice/util/Dd", Dd,
                          sizeof(Dd) / sizeof(Dd[0]));

    return JNI_TRUE;
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    register_ndk_load(env);

    // 返回jni的版本
    return JNI_VERSION_1_4;
}
