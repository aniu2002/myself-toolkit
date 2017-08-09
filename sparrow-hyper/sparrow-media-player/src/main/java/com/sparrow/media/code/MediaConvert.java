package com.sparrow.media.code;

public class MediaConvert {
	public static void main(String args[]) {
		String videoPath = "F:\\webserver\\red5\\webapps\\vod\\mp4\\lyl-hktk.mp4";
		String targetPath = "F:\\_store\\temp\\2014-4-19\\lyl-hktk.flv";
		ConvertVideo cv = new ConvertVideo(videoPath, targetPath);
		cv.process();
	}
}
