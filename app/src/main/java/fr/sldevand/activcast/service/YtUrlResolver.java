package fr.sldevand.activcast.service;

import android.content.Context;
import android.util.SparseArray;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import fr.sldevand.activcast.utils.Toaster;

public class YtUrlResolver {
    private static final int ITAG_720 = 22;
    private static final int ITAG_360 = 18;

    private static OnResolvedUrlListener onResolvedUrlListener;

    public static void resolve(final Context context, final String youtubeLink) {
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {

                try {
                    if (null == ytFiles) {
                        notAvailableFormatsError(youtubeLink);
                        return;
                    }

                    YtFile ytFile = ytFiles.get(ITAG_720);
                    if (null == ytFile) {
                        ytFile = ytFiles.get(ITAG_360);
                    }

                    if (null == ytFile) {
                        notAvailableFormatError(youtubeLink);
                        return;
                    }

                    String downloadUrl = ytFile.getUrl();
                    if(null != downloadUrl && null != onResolvedUrlListener) {
                        onResolvedUrlListener.onResolvedUrl(downloadUrl);
                    }

                } catch (Exception exception) {
                    Toaster.longToast(context, exception.getMessage());
                }
            }
        }.extract(youtubeLink, true, true);
    }

    private static void notAvailableFormatsError(String youtubeLink) {
        onResolvedUrlListener.onError("There is no available High quality format for this video", youtubeLink);
    }

    private static void notAvailableFormatError(String youtubeLink) {
        onResolvedUrlListener.onError("No youtube links could be extracted", youtubeLink);
    }

    public void setResolvedUrlListener(OnResolvedUrlListener listener) {
        onResolvedUrlListener = listener;
    }

    public interface OnResolvedUrlListener {
        void onResolvedUrl(String url);
        void onError(String message, String youtubeLink);
    }
}
