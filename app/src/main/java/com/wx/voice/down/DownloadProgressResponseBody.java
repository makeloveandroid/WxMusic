package com.wx.voice.down;

import com.wx.voice.util.L;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class DownloadProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private DownloadProgressListener progressListener;
    private BufferedSource bufferedSource;
    private final PublishSubject<DownValue> objectPublishSubject;

    public DownloadProgressResponseBody(ResponseBody responseBody,
                                        final DownloadProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        objectPublishSubject = PublishSubject
                .create();

        objectPublishSubject
                .sample(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownValue>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DownValue downValue) {
                        progressListener.update(downValue.cuurentDwon, downValue.count, downValue.isDone);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public static class DownValue {
        public long count;
        public long cuurentDwon;
        public boolean isDone;

        public DownValue(long count, long cuurentDwon, boolean isDone) {
            this.count = count;
            this.cuurentDwon = cuurentDwon;
            this.isDone = isDone;
        }
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                final long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                if (null != progressListener) {
                    DownValue downValue = new DownValue(responseBody.contentLength(), totalBytesRead, bytesRead == -1);
                    objectPublishSubject.onNext(downValue);
                    if (bytesRead == -1) {
                        objectPublishSubject.onComplete();
                    }
                }
                return bytesRead;
            }
        };

    }
}
