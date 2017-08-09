进入jdk的bin目录：

1、生成server.keystore
     --- # keytool -genkey -keyalg RSA -alias nettySSL -dname "cn=localhost" -keystore D:/security/server.keystore -storepass yzc007
     keytool -genkey -v -alias serverKey -dname "CN=localhost" -keyalg RSA -keypass yzc007 -keystore D:/security/server.ks -storepass yzc007
     密钥和密钥库密码 yzc007

 /*
    keytool -genkeypair -v -alias serverKey -dname "CN=localhost" -keyalg RSA -keypass yzc007 -validity 7 -keystore D:/security/server.ks -storepass yzc007
    ---> keytool -genkey -v -alias selfKey -dname "CN=localhost" -keyalg RSA -keypass yzc007 -keystore D:/security/server.ks -storepass yzc007
    keytool -export -alias serverKey -keystore D:/security/server.ks -rfc -file D:/security/server.cer -storepass yzc007
    ---> keytool -export -alias selfKey -file D:/security/server.cer -keystore D:/security/server.ks -storepass yzc007 -storetype PKCS12
    keytool -import -v -alias serverKey -file D:/security/server.cer -keystore D:/security/trust.ks -storepass yzc007
    keytool -list -v -keystore D:/security/trust.ks -storepass yzc007
  */

2、生成client.keystore
     keytool -genkey -v -alias clientKey -dname "CN=SomeOne" -keyalg RSA -keypass test001 -keystore D:/security/client.ks -storepass test001 -storetype PKCS12

3、将客户端密钥导出为证书文件
     keytool -export -alias clientKey -file D:/security/clientKey.cer -keystore D:/security/client.ks -storepass test001 -storetype PKCS12

4、将上述客户端密钥文件导入服务器证书库，并设置为信任证书；注意会问你是否信任该证书，回答 y 即可
     keytool -import -v -alias clientKey -file D:/security/clientKey.cer -keystore D:/security/trust.ks -storepass yzc007