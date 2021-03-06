package com.org.ffmpeg;

import com.vantageclient.decoder.VideoTypeHelper;

public class FFmpegDemo {

	static {

		System.loadLibrary("ffmpeg");
		System.loadLibrary("TestFFmpegJni");
		_codec = -1;
	}

	public native int GetVersion();

	public native String GetString();

	public native int init(int codec);

	public native int decode(byte[] in, int length, byte[] out);

	public native int getwidth();

	public native int getheight();

	public native int uninit();

	private static int _codec;

	public void setCodec(int value) {
		_codec = value;
	}

	public int getCodec() {
		return _codec;
	}

	public static int getCodecType(byte[] buffer) {
		return VideoTypeHelper.GetVideoType(buffer).value();
	}

	public enum CodecID {
		CODEC_ID_NOSET(-1), CODEC_ID_NONE(0), CODEC_ID_MJPEG(8), CODEC_ID_MPEG4(
				13), CODEC_ID_H264(28);

		private int _value;

		private CodecID(int value) {
			_value = value;
		}

		public int value() {
			return _value;
		}
	}

	// public enum CodecID {
	// CODEC_ID_NONE,
	//
	// /* video codecs */
	// CODEC_ID_MPEG1VIDEO,
	// CODEC_ID_MPEG2VIDEO, ///< preferred ID for MPEG-1/2 video decoding
	// CODEC_ID_MPEG2VIDEO_XVMC,
	// CODEC_ID_H261,
	// CODEC_ID_H263,
	// CODEC_ID_RV10,
	// CODEC_ID_RV20,
	// CODEC_ID_MJPEG, //8
	// CODEC_ID_MJPEGB,
	// CODEC_ID_LJPEG,
	// CODEC_ID_SP5X,
	// CODEC_ID_JPEGLS,
	// CODEC_ID_MPEG4, //13
	// CODEC_ID_RAWVIDEO,
	// CODEC_ID_MSMPEG4V1,
	// CODEC_ID_MSMPEG4V2,
	// CODEC_ID_MSMPEG4V3,
	// CODEC_ID_WMV1,
	// CODEC_ID_WMV2,
	// CODEC_ID_H263P,
	// CODEC_ID_H263I,
	// CODEC_ID_FLV1,
	// CODEC_ID_SVQ1,
	// CODEC_ID_SVQ3,
	// CODEC_ID_DVVIDEO,
	// CODEC_ID_HUFFYUV,
	// CODEC_ID_CYUV,
	// CODEC_ID_H264,
	// CODEC_ID_INDEO3,
	// CODEC_ID_VP3,
	// CODEC_ID_THEORA,
	// CODEC_ID_ASV1,
	// CODEC_ID_ASV2,
	// CODEC_ID_FFV1,
	// CODEC_ID_4XM,
	// CODEC_ID_VCR1,
	// CODEC_ID_CLJR,
	// CODEC_ID_MDEC,
	// CODEC_ID_ROQ,
	// CODEC_ID_INTERPLAY_VIDEO,
	// CODEC_ID_XAN_WC3,
	// CODEC_ID_XAN_WC4,
	// CODEC_ID_RPZA,
	// CODEC_ID_CINEPAK,
	// CODEC_ID_WS_VQA,
	// CODEC_ID_MSRLE,
	// CODEC_ID_MSVIDEO1,
	// CODEC_ID_IDCIN,
	// CODEC_ID_8BPS,
	// CODEC_ID_SMC,
	// CODEC_ID_FLIC,
	// CODEC_ID_TRUEMOTION1,
	// CODEC_ID_VMDVIDEO,
	// CODEC_ID_MSZH,
	// CODEC_ID_ZLIB,
	// CODEC_ID_QTRLE,
	// CODEC_ID_SNOW,
	// CODEC_ID_TSCC,
	// CODEC_ID_ULTI,
	// CODEC_ID_QDRAW,
	// CODEC_ID_VIXL,
	// CODEC_ID_QPEG,
	// CODEC_ID_PNG,
	// CODEC_ID_PPM,
	// CODEC_ID_PBM,
	// CODEC_ID_PGM,
	// CODEC_ID_PGMYUV,
	// CODEC_ID_PAM,
	// CODEC_ID_FFVHUFF,
	// CODEC_ID_RV30,
	// CODEC_ID_RV40,
	// CODEC_ID_VC1,
	// CODEC_ID_WMV3,
	// CODEC_ID_LOCO,
	// CODEC_ID_WNV1,
	// CODEC_ID_AASC,
	// CODEC_ID_INDEO2,
	// CODEC_ID_FRAPS,
	// CODEC_ID_TRUEMOTION2,
	// CODEC_ID_BMP,
	// CODEC_ID_CSCD,
	// CODEC_ID_MMVIDEO,
	// CODEC_ID_ZMBV,
	// CODEC_ID_AVS,
	// CODEC_ID_SMACKVIDEO,
	// CODEC_ID_NUV,
	// CODEC_ID_KMVC,
	// CODEC_ID_FLASHSV,
	// CODEC_ID_CAVS,
	// CODEC_ID_JPEG2000,
	// CODEC_ID_VMNC,
	// CODEC_ID_VP5,
	// CODEC_ID_VP6,
	// CODEC_ID_VP6F,
	// CODEC_ID_TARGA,
	// CODEC_ID_DSICINVIDEO,
	// CODEC_ID_TIERTEXSEQVIDEO,
	// CODEC_ID_TIFF,
	// CODEC_ID_GIF,
	// CODEC_ID_FFH264,
	// CODEC_ID_DXA,
	// CODEC_ID_DNXHD,
	// CODEC_ID_THP,
	// CODEC_ID_SGI,
	// CODEC_ID_C93,
	// CODEC_ID_BETHSOFTVID,
	// CODEC_ID_PTX,
	// CODEC_ID_TXD,
	// CODEC_ID_VP6A,
	// CODEC_ID_AMV,
	// CODEC_ID_VB,
	// CODEC_ID_PCX,
	// CODEC_ID_SUNRAST,
	// CODEC_ID_INDEO4,
	// CODEC_ID_INDEO5,
	// CODEC_ID_MIMIC,
	// CODEC_ID_RL2,
	// CODEC_ID_8SVX_EXP,
	// CODEC_ID_8SVX_FIB,
	// CODEC_ID_ESCAPE124,
	// CODEC_ID_DIRAC,
	// CODEC_ID_BFI,
	// CODEC_ID_CMV,
	// CODEC_ID_MOTIONPIXELS,
	// CODEC_ID_TGV,
	// CODEC_ID_TGQ,
	// CODEC_ID_TQI,
	// CODEC_ID_AURA,
	// CODEC_ID_AURA2,
	// CODEC_ID_V210X,
	// CODEC_ID_TMV,
	// CODEC_ID_V210,
	// CODEC_ID_DPX,
	// CODEC_ID_MAD,
	// CODEC_ID_FRWU,
	// CODEC_ID_FLASHSV2,
	// CODEC_ID_CDGRAPHICS,
	// CODEC_ID_R210,
	// CODEC_ID_ANM,
	// CODEC_ID_BINKVIDEO,
	// CODEC_ID_IFF_ILBM,
	// CODEC_ID_IFF_BYTERUN1,
	// CODEC_ID_KGV1,
	// CODEC_ID_YOP,
	// CODEC_ID_VP8,
	// CODEC_ID_PICTOR,
	// CODEC_ID_ANSI,
	// CODEC_ID_A64_MULTI,
	// CODEC_ID_A64_MULTI5,
	// CODEC_ID_R10K,
	// CODEC_ID_MXPEG,
	// CODEC_ID_LAGARITH,
	// CODEC_ID_PRORES,
	// CODEC_ID_JV,
	// CODEC_ID_DFA,
	// CODEC_ID_8SVX_RAW,
	// };
}
