package org.robolectric.shadows;

import static android.media.MediaFormat.MIMETYPE_AUDIO_AAC;
import static android.media.MediaFormat.MIMETYPE_AUDIO_OPUS;
import static android.media.MediaFormat.MIMETYPE_VIDEO_AVC;
import static android.media.MediaFormat.MIMETYPE_VIDEO_VP9;
import static android.os.Build.VERSION_CODES.Q;
import static com.google.common.truth.Truth.assertThat;

import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaFormat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

/** Tests for {@link MediaCodecInfoBuilder}. */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = Q)
public class MediaCodecInfoBuilderTest {

  private static final String AAC_ENCODER_NAME = "test.encoder.aac";
  private static final String VP9_DECODER_NAME = "test.decoder.vp9";

  private static final MediaFormat AAC_MEDIA_FORMAT =
      createMediaFormat(
          MIMETYPE_AUDIO_AAC, new String[] {CodecCapabilities.FEATURE_DynamicTimestamp});
  private static final MediaFormat OPUS_MEDIA_FORMAT =
      createMediaFormat(
          MIMETYPE_AUDIO_OPUS, new String[] {CodecCapabilities.FEATURE_AdaptivePlayback});
  private static final MediaFormat AVC_MEDIA_FORMAT =
      createMediaFormat(MIMETYPE_VIDEO_AVC, new String[] {CodecCapabilities.FEATURE_IntraRefresh});
  private static final MediaFormat VP9_MEDIA_FORMAT =
      createMediaFormat(
          MIMETYPE_VIDEO_VP9,
          new String[] {
            CodecCapabilities.FEATURE_SecurePlayback, CodecCapabilities.FEATURE_MultipleFrames
          });

  private static final CodecProfileLevel[] AAC_PROFILE_LEVELS =
      new CodecProfileLevel[] {
        createCodecProfileLevel(CodecProfileLevel.AACObjectELD, 0),
        createCodecProfileLevel(CodecProfileLevel.AACObjectHE, 1)
      };
  private static final CodecProfileLevel[] AVC_PROFILE_LEVELS =
      new CodecProfileLevel[] {
        createCodecProfileLevel(CodecProfileLevel.AVCProfileMain, CodecProfileLevel.AVCLevel12)
      };
  private static final CodecProfileLevel[] VP9_PROFILE_LEVELS =
      new CodecProfileLevel[] {
        createCodecProfileLevel(CodecProfileLevel.VP9Profile3, CodecProfileLevel.VP9Level52)
      };

  private static final int[] AVC_COLOR_FORMATS =
      new int[] {
        CodecCapabilities.COLOR_FormatYUV420Flexible, CodecCapabilities.COLOR_FormatYUV420Planar
      };
  private static final int[] VP9_COLOR_FORMATS =
      new int[] {
        CodecCapabilities.COLOR_FormatYUV422Flexible, CodecCapabilities.COLOR_Format32bitABGR8888
      };

  @Test
  public void aacEncoderCapabilities() {
    CodecCapabilities aacCapabilities =
        MediaCodecInfoBuilder.CodecCapabilitiesBuilder.create()
            .setMediaFormat(AAC_MEDIA_FORMAT)
            .setIsEncoder(true)
            .setProfileLevels(AAC_PROFILE_LEVELS)
            .build();

    assertThat(aacCapabilities.getMimeType()).isEqualTo(MIMETYPE_AUDIO_AAC);
    assertThat(aacCapabilities.getAudioCapabilities()).isNotNull();
    assertThat(aacCapabilities.getVideoCapabilities()).isNull();
    assertThat(aacCapabilities.getEncoderCapabilities()).isNotNull();
    assertThat(aacCapabilities.isFeatureSupported(CodecCapabilities.FEATURE_DynamicTimestamp))
        .isTrue();
    assertThat(aacCapabilities.isFeatureSupported(CodecCapabilities.FEATURE_FrameParsing))
        .isFalse();
    assertThat(aacCapabilities.profileLevels).hasLength(AAC_PROFILE_LEVELS.length);
    assertThat(aacCapabilities.profileLevels).isEqualTo(AAC_PROFILE_LEVELS);
  }

  @Test
  public void opusDecoderCapabilities() {
    CodecCapabilities opusCapabilities =
        MediaCodecInfoBuilder.CodecCapabilitiesBuilder.create()
            .setMediaFormat(OPUS_MEDIA_FORMAT)
            .setProfileLevels(new CodecProfileLevel[0])
            .build();

    assertThat(opusCapabilities.getMimeType()).isEqualTo(MIMETYPE_AUDIO_OPUS);
    assertThat(opusCapabilities.getAudioCapabilities()).isNotNull();
    assertThat(opusCapabilities.getVideoCapabilities()).isNull();
    assertThat(opusCapabilities.getEncoderCapabilities()).isNull();
    assertThat(opusCapabilities.isFeatureSupported(CodecCapabilities.FEATURE_AdaptivePlayback))
        .isTrue();
    assertThat(opusCapabilities.isFeatureSupported(CodecCapabilities.FEATURE_MultipleFrames))
        .isFalse();
    assertThat(opusCapabilities.profileLevels).hasLength(0);
  }

  @Test
  public void avcEncoderCapabilities() {
    CodecCapabilities avcCapabilities =
        MediaCodecInfoBuilder.CodecCapabilitiesBuilder.create()
            .setMediaFormat(AVC_MEDIA_FORMAT)
            .setIsEncoder(true)
            .setProfileLevels(AVC_PROFILE_LEVELS)
            .setColorFormats(AVC_COLOR_FORMATS)
            .build();

    assertThat(avcCapabilities.getMimeType()).isEqualTo(MIMETYPE_VIDEO_AVC);
    assertThat(avcCapabilities.getAudioCapabilities()).isNull();
    assertThat(avcCapabilities.getVideoCapabilities()).isNotNull();
    assertThat(avcCapabilities.getEncoderCapabilities()).isNotNull();
    assertThat(avcCapabilities.isFeatureSupported(CodecCapabilities.FEATURE_IntraRefresh)).isTrue();
    assertThat(avcCapabilities.isFeatureSupported(CodecCapabilities.FEATURE_MultipleFrames))
        .isFalse();
    assertThat(avcCapabilities.profileLevels).hasLength(AVC_PROFILE_LEVELS.length);
    assertThat(avcCapabilities.profileLevels).isEqualTo(AVC_PROFILE_LEVELS);
    assertThat(avcCapabilities.colorFormats).hasLength(AVC_COLOR_FORMATS.length);
    assertThat(avcCapabilities.colorFormats).isEqualTo(AVC_COLOR_FORMATS);
  }

  @Test
  public void vp9DecoderCapabilities() {
    CodecCapabilities vp9Capabilities =
        MediaCodecInfoBuilder.CodecCapabilitiesBuilder.create()
            .setMediaFormat(VP9_MEDIA_FORMAT)
            .setProfileLevels(VP9_PROFILE_LEVELS)
            .setColorFormats(VP9_COLOR_FORMATS)
            .build();

    assertThat(vp9Capabilities.getMimeType()).isEqualTo(MIMETYPE_VIDEO_VP9);
    assertThat(vp9Capabilities.getAudioCapabilities()).isNull();
    assertThat(vp9Capabilities.getVideoCapabilities()).isNotNull();
    assertThat(vp9Capabilities.getEncoderCapabilities()).isNull();
    assertThat(vp9Capabilities.isFeatureSupported(CodecCapabilities.FEATURE_SecurePlayback))
        .isTrue();
    assertThat(vp9Capabilities.isFeatureSupported(CodecCapabilities.FEATURE_MultipleFrames))
        .isTrue();
    assertThat(vp9Capabilities.isFeatureSupported(CodecCapabilities.FEATURE_DynamicTimestamp))
        .isFalse();
    assertThat(vp9Capabilities.profileLevels).hasLength(VP9_PROFILE_LEVELS.length);
    assertThat(vp9Capabilities.profileLevels).isEqualTo(VP9_PROFILE_LEVELS);
    assertThat(vp9Capabilities.colorFormats).hasLength(VP9_COLOR_FORMATS.length);
    assertThat(vp9Capabilities.colorFormats).isEqualTo(VP9_COLOR_FORMATS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildWithoutMediaFormatBeingSet() {
    CodecCapabilities caps =
        MediaCodecInfoBuilder.CodecCapabilitiesBuilder.create()
            .setProfileLevels(AVC_PROFILE_LEVELS)
            .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildWithoutProfileLevelsBeingSet() {
    MediaFormat mediaFormat = createMediaFormat(MIMETYPE_VIDEO_AVC, /* features= */ new String[0]);
    CodecCapabilities caps =
        MediaCodecInfoBuilder.CodecCapabilitiesBuilder.create().setMediaFormat(mediaFormat).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildWithoutColorFormatsBeingSet() {
    MediaFormat mediaFormat = createMediaFormat(MIMETYPE_VIDEO_AVC, /* features= */ new String[0]);
    CodecCapabilities caps =
        MediaCodecInfoBuilder.CodecCapabilitiesBuilder.create()
            .setMediaFormat(mediaFormat)
            .setProfileLevels(AVC_PROFILE_LEVELS)
            .build();
  }

  @Test
  public void aacEncoderMediaCodecInfo() {
    CodecCapabilities aacCapabilities =
        MediaCodecInfoBuilder.CodecCapabilitiesBuilder.create()
            .setMediaFormat(AAC_MEDIA_FORMAT)
            .setIsEncoder(true)
            .setProfileLevels(AAC_PROFILE_LEVELS)
            .setColorFormats(new int[0]) // To prevent crash in CodecCapabilities.dup().
            .build();

    MediaCodecInfo aacEncoderInfo =
        MediaCodecInfoBuilder.create()
            .setName(AAC_ENCODER_NAME)
            .setFlags(MediaCodecInfoBuilder.FLAG_IS_ENCODER | MediaCodecInfoBuilder.FLAG_IS_VENDOR)
            .setCapabilities(new CodecCapabilities[] {aacCapabilities})
            .build();

    assertThat(aacEncoderInfo.getName()).isEqualTo(AAC_ENCODER_NAME);
    assertThat(aacEncoderInfo.isEncoder()).isTrue();
    assertThat(aacEncoderInfo.isVendor()).isTrue();
    assertThat(aacEncoderInfo.isSoftwareOnly()).isFalse();
    assertThat(aacEncoderInfo.isHardwareAccelerated()).isFalse();
    assertThat(aacEncoderInfo.getSupportedTypes()).asList().containsExactly(MIMETYPE_AUDIO_AAC);
    assertThat(aacEncoderInfo.getCapabilitiesForType(MIMETYPE_AUDIO_AAC)).isNotNull();
  }

  @Test
  public void vp9DecoderMediaCodecInfo() {
    CodecCapabilities vp9Capabilities =
        MediaCodecInfoBuilder.CodecCapabilitiesBuilder.create()
            .setMediaFormat(VP9_MEDIA_FORMAT)
            .setProfileLevels(VP9_PROFILE_LEVELS)
            .setColorFormats(VP9_COLOR_FORMATS)
            .build();

    MediaCodecInfo vp9DecoderInfo =
        MediaCodecInfoBuilder.create()
            .setName(VP9_DECODER_NAME)
            .setFlags(MediaCodecInfoBuilder.FLAG_IS_HARDWARE_ACCELERATED)
            .setCapabilities(new CodecCapabilities[] {vp9Capabilities})
            .build();

    assertThat(vp9DecoderInfo.getName()).isEqualTo(VP9_DECODER_NAME);
    assertThat(vp9DecoderInfo.isEncoder()).isFalse();
    assertThat(vp9DecoderInfo.isVendor()).isFalse();
    assertThat(vp9DecoderInfo.isSoftwareOnly()).isFalse();
    assertThat(vp9DecoderInfo.isHardwareAccelerated()).isTrue();
    assertThat(vp9DecoderInfo.getSupportedTypes()).asList().containsExactly(MIMETYPE_VIDEO_VP9);
    assertThat(vp9DecoderInfo.getCapabilitiesForType(MIMETYPE_VIDEO_VP9)).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void setUnsupportedFlags() {
    MediaCodecInfo vp9DecoderInfo =
        MediaCodecInfoBuilder.create().setFlags(~MediaCodecInfoBuilder.FLAGS_ALL).build();
  }

  /** Create a sample {@link CodecProfileLevel}. */
  private static CodecProfileLevel createCodecProfileLevel(int profile, int level) {
    CodecProfileLevel profileLevel = new CodecProfileLevel();
    profileLevel.profile = profile;
    profileLevel.level = level;
    return profileLevel;
  }

  /**
   * Create a sample {@link MediaFormat}.
   *
   * @param mime one of MIMETYPE_* from {@link MediaFormat}.
   * @param features an array of CodecCapabilities.FEATURE_ features to be enabled.
   */
  private static MediaFormat createMediaFormat(String mime, String[] features) {
    MediaFormat mediaFormat = new MediaFormat();
    mediaFormat.setString(MediaFormat.KEY_MIME, mime);
    for (String feature : features) {
      mediaFormat.setFeatureEnabled(feature, true);
    }
    return mediaFormat;
  }
}
