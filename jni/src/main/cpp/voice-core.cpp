#include <jni.h>
#include <string>
#include<fstream>

const char HexCode[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
                        'F'};


int copy(JNIEnv *env, jstring jsrc, jstring jdst) {
    const char *src = env->GetStringUTFChars(jsrc, 0);
    const char *dst = env->GetStringUTFChars(jdst, 0);
    using namespace std;
    ifstream in(src, ios::binary);
    ofstream out(dst, ios::binary);
    if (!in.is_open()) {
        return 0;
    }
    if (!out.is_open()) {
        return 0;
    }
    if (src == dst) {
        return 0;
    }
    char buf[2048];
    long long totalBytes = 0;
    while (in) {
        //read从in流中读取2048字节，放入buf数组中，同时文件指针向后移动2048字节
        //若不足2048字节遇到文件结尾，则以实际提取字节读取。
        in.read(buf, 2048);
        //gcount()用来提取读取的字节数，write将buf中的内容写入out流。
        out.write(buf, in.gcount());
        totalBytes += in.gcount();
    }
    in.close();
    out.close();
    env->ReleaseStringUTFChars(jsrc, src);
    env->ReleaseStringUTFChars(jdst, dst);
    return 1;
}

//extern "C"
//__attribute__((section (".mytext")))
JNIEXPORT jstring JNICALL
sv(JNIEnv *env, jclass clz, jobject context, jstring id, jstring path, jint time) {
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
        // 1:创建IDclass 搜索 startRecord insert voicestg success
        char *mmCreateClass = "com/tencent/mm/modelvoice/q";
        // 创建ID的方法 上面找到的类搜索 startRecord insert voicestg success
        char *mmCreateMethod = "uo";

        // 获取新路径方法 上面IDclass 搜索 getFullPath 或者 寻找下面方法(一般搜索 getAmrFullPath cost: 上面的方法就是)
        /**
         *     public static String getFullPath(String str) {
                if (bp.isNullOrNil(str)) {
                   return null;
                   }
                 return T(str, false);
               }
         */
        char *mmGetFullPathMethod = "getFullPath";
        // 入库 上面IDclass 搜索类似此方法 一般搜索字符串 StopRecord fileName[ 上面的那个方法就是
        /**
         *   public static boolean ab(String str, int i) {
                return o(str, i, 0);
             }
         */
        char *mmSqlMethod = "af";
        // 构造语音对象类 搜索 Checksum error file. cacheSimpleChecksum:%d, realSimpleChecksum:%d
        char *mmObjClass = "com/tencent/mm/modelvoice/f";

        // 网络请求方法单利类
        // 搜索 MicroMsg.MMCore", "[doAccountPostReset] begin to updated HardCodeUpdate 确定是那个类 这个是内部类的
        char *mmNetClass = "com/tencent/mm/model/av";
        // 单利方法 搜索这句话的MMCore has not been initialize ? 下面第一个方法就是
        char *mmNetMethod = "Pw";
        // 单利返回核心对象方法签名(其实就是单利返回的对象类型) 对应上面返回的值
        char *mmNetSingle = "()Lcom/tencent/mm/ah/p;";
        // 调用发送请求 搜索 worker thread has not been set 上面的第一个方法就是
        char *mmCallMethod = "d";
        // 搜索 scene security verification not passed, type=
        char *mmCallSingle = "(Lcom/tencent/mm/ah/m;)Z";


        jclass qClzz = env->FindClass(mmCreateClass);

        jmethodID pID = env->GetStaticMethodID(qClzz, mmCreateMethod,
                                               "(Ljava/lang/String;)Ljava/lang/String;");

        jobject newId = env->CallStaticObjectMethod(qClzz, pID, id);


        jmethodID pID2 = env->GetStaticMethodID(qClzz, mmGetFullPathMethod,
                                                "(Ljava/lang/String;)Ljava/lang/String;");

        jstring newPath = static_cast<jstring>(env->CallStaticObjectMethod(qClzz, pID2, newId));

        // todo 注意这里是调用了自己的方法

        jboolean flag = copy(env, path, newPath);
        if (!flag) {
            return env->NewStringUTF("拷贝文件出错");
        }
        // com.tencent.mm.modelvoice.q.Y(newId, time);
        jmethodID pID4 = env->GetStaticMethodID(qClzz, mmSqlMethod, "(Ljava/lang/String;I)Z");


        jboolean flag2 = env->CallStaticBooleanMethod(qClzz, pID4, newId, time);
        if (!flag2) {
            return env->NewStringUTF("入库出错");
        }
        // com.tencent.mm.modelvoice.f f = new com.tencent.mm.modelvoice.f(newId);
        jclass Fclass = env->FindClass(mmObjClass);

        jmethodID pId5 = env->GetMethodID(Fclass, "<init>", "(Ljava/lang/String;)V");

        jobject jObj = env->NewObject(Fclass, pId5, newId);

        //  com.tencent.mm.y.as.ys().d(f); //ys()返回 com.tencent.mm.ad.n
        jclass coreClzz = env->FindClass(mmNetClass);

        jmethodID pid6 = env->GetStaticMethodID(coreClzz, mmNetMethod, mmNetSingle);

        jobject coreObj = env->CallStaticObjectMethod(coreClzz, pid6);

        jclass nclass = env->GetObjectClass(coreObj);
        jmethodID pid7 = env->GetMethodID(nclass, mmCallMethod, mmCallSingle);

        jboolean i = env->CallBooleanMethod(coreObj, pid7, jObj);
        env->DeleteLocalRef(nclass);
        env->DeleteLocalRef(qClzz);
        env->DeleteLocalRef(coreClzz);
        env->DeleteLocalRef(Fclass);
        return env->NewStringUTF("OK");
    }
    env->DeleteLocalRef(x_509_jstring);
    env->DeleteLocalRef(sha1_jstring);
    return env->NewStringUTF("OK");
}


static JNINativeMethod voice[] = {
        {"SV", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;I)V", (void *) sv},
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
    registerNativeMethods(env, "com/nine/remotemm/util/VC", voice,
                          sizeof(voice) / sizeof(voice[0]));

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
