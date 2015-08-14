package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import javax.net.ssl.SSLSocketFactory;

/**
 * @author Hongjian.Liu 3:03:39 PM Mar 6, 2014
 *         <p/>
 *         CustomVolley is one custom implement of Volley{#Volley},CustomVolley use
 *         {@link NoCache} instead of {@link com.android.volley.toolbox.DiskBasedCache}.
 */
public class
        CustomVolley {
    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";
    private static final int ThreadPoolSize = 5;
    public static final String CRT = "test.crt";

    /**
     * Creates a default instance of the worker pool and calls
     * {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack   An {@link HttpStack} to use for the network, or null for
     *                default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, Cache cache, HttpStack stack) {
        String userAgent = "kvolley/0";
        VolleyLog.setTag("CustomVolley");
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }
        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                SSLSocketFactory sslSocketFactory = null;
                try {
                    sslSocketFactory = getSSLSocketFactory(context);
                } catch (Exception e) {
                }
                stack = new HurlStack(null, sslSocketFactory);
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See:
                // http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }
        Network network = new BasicNetwork(stack);
        RequestQueue queue;
        if (cache == null) {
            queue = new RequestQueue(new NoCache(), network, ThreadPoolSize);
        } else {
            queue = new RequestQueue(cache, network);
        }
        queue.start();
        return queue;
    }

    private static SSLSocketFactory getSSLSocketFactory(Context context) {
        return null;
    }
//    private static SSLSocketFactory getSSLSocketFactory(Context context) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        // Load CAs from an InputStream
//        // (could be from a resource or ByteArrayInputStream or ...)
//        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//        //
//        InputStream caInput = new BufferedInputStream(context.getAssets().open(CRT));
//        Certificate ca;
//        try {
//            ca = cf.generateCertificate(caInput);
//            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
//        } finally {
//            caInput.close();
//        }
//        // Create a KeyStore containing our trusted CAs
//        String keyStoreType = KeyStore.getDefaultType();
//        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//        keyStore.load(null, null);
//        keyStore.setCertificateEntry("ca", ca);
//        // Create a TrustManager that trusts the CAs in our KeyStore
//        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//        tmf.init(keyStore);
//        // Create an SSLContext that uses our TrustManager
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, tmf.getTrustManagers(), null);
//        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//        return sslSocketFactory;
//    }

    /**
     * Creates a default instance of the worker pool and calls
     * {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, Cache cache) {
        return newRequestQueue(context, cache, null);
    }

    /**
     * Creates a default instance of the worker pool and calls
     * {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null, null);
    }
}
