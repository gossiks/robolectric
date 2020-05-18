package org.robolectric.shadows;

import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.AudioCapabilities;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.EncoderCapabilities;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.media.MediaFormat;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;

/** Builder for {@link MediaCodecInfo}. */
public class MediaCodecInfoBuilder {

  // Replica of internal MediaCodecInfo flags.
  public static final int FLAG_IS_ENCODER =
      (int) ReflectionHelpers.getStaticField(MediaCodecInfo.class, "FLAG_IS_ENCODER");
  public static final int FLAG_IS_VENDOR =
      (int) ReflectionHelpers.getStaticField(MediaCodecInfo.class, "FLAG_IS_VENDOR");
  public static final int FLAG_IS_SOFTWARE_ONLY =
      (int) ReflectionHelpers.getStaticField(MediaCodecInfo.class, "FLAG_IS_SOFTWARE_ONLY");
  public static final int FLAG_IS_HARDWARE_ACCELERATED =
      (int) ReflectionHelpers.getStaticField(MediaCodecInfo.class, "FLAG_IS_HARDWARE_ACCELERATED");
  public static final int FLAGS_ALL =
      FLAG_IS_ENCODER | FLAG_IS_VENDOR | FLAG_IS_SOFTWARE_ONLY | FLAG_IS_HARDWARE_ACCELERATED;

  private String name;
  private int flags;
  private CodecCapabilities[] capabilities;

  private MediaCodecInfoBuilder() {}

  /** Create a new {@link MediaCodecInfoBuilder}. */
  public static MediaCodecInfoBuilder create() {
    return new MediaCodecInfoBuilder();
  }

  /** Sets codec name. */
  public MediaCodecInfoBuilder setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets codec flags describing role and implementation details of the codec.
   *
   * @param flags bit mask of FLAG_*. Throws {@link IllegalArgumentException} if mask has
   *     unsupported flags.
   */
  public MediaCodecInfoBuilder setFlags(int flags) {
    if ((flags & ~FLAGS_ALL) != 0) {
      throw new IllegalArgumentException("Unsupported flags: " + flags);
    }
    this.flags = flags;
    return this;
  }

  /**
   * Sets codec capabilities. A codec capabilities instance can be created using {@link
   * CodecCapabilitiesBuilder}.
   */
  public MediaCodecInfoBuilder setCapabilities(CodecCapabilities[] capabilities) {
    this.capabilities = capabilities;
    return this;
  }

  public MediaCodecInfo build() {
    return ReflectionHelpers.callConstructor(
        MediaCodecInfo.class,
        ClassParameter.from(String.class, name),
        ClassParameter.from(String.class, name), // canonicalName
        ClassParameter.from(int.class, flags),
        ClassParameter.from(CodecCapabilities[].class, capabilities));
  }

  /** Builder for {@link CodecCapabilities}. */
  public static class CodecCapabilitiesBuilder {
    private MediaFormat mediaFormat;
    private boolean isEncoder;
    private CodecProfileLevel[] profileLevels;
    private int[] colorFormats;

    private CodecCapabilitiesBuilder() {}

    /** Creates a new {@link CodecCapabilitiesBuilder}. */
    public static CodecCapabilitiesBuilder create() {
      return new CodecCapabilitiesBuilder();
    }

    /** Sets media format. */
    public CodecCapabilitiesBuilder setMediaFormat(MediaFormat mediaFormat) {
      this.mediaFormat = mediaFormat;
      return this;
    }

    /** Sets a flag which indicates whether this codec is an encoder or a decoder. */
    public CodecCapabilitiesBuilder setIsEncoder(boolean isEncoder) {
      this.isEncoder = isEncoder;
      return this;
    }

    /**
     * Sets profiles and levels.
     *
     * @param profileLevels {@link MediaCodecInfo.CodecProfileLevel} supported by the codec.
     */
    public CodecCapabilitiesBuilder setProfileLevels(CodecProfileLevel[] profileLevels) {
      this.profileLevels = profileLevels;
      return this;
    }

    /**
     * Sets color formats.
     *
     * @param colorFormats color formats supported by the codec. Refer to {@link CodecCapabilities}
     *     for possible values.
     */
    public CodecCapabilitiesBuilder setColorFormats(int[] colorFormats) {
      this.colorFormats = colorFormats;
      return this;
    }

    public CodecCapabilities build() {
      if (mediaFormat == null) {
        throw new IllegalArgumentException("Media format is not specified.");
      }
      if (profileLevels == null) {
        throw new IllegalArgumentException("Codec profile and levels are not specified.");
      }

      final String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
      final boolean isVideoCodec = mime.toLowerCase().startsWith("video/");

      if (isVideoCodec && colorFormats == null) {
        throw new IllegalArgumentException("Color formats are not specified.");
      }

      CodecCapabilities caps = new CodecCapabilities();

      caps.profileLevels = profileLevels;
      if (colorFormats != null) {
        caps.colorFormats = colorFormats;
      } else {
        caps.colorFormats = new int[0]; // To prevet crash in CodecCapabilities.dup().
      }

      ReflectionHelpers.setField(caps, "mMime", mime);
      ReflectionHelpers.setField(caps, "mMaxSupportedInstances", 32);
      ReflectionHelpers.setField(caps, "mDefaultFormat", mediaFormat);
      ReflectionHelpers.setField(caps, "mCapabilitiesInfo", mediaFormat);

      if (isVideoCodec) {
        VideoCapabilities videoCaps = createDefaultVideoCapabilities(caps, mediaFormat);
        ReflectionHelpers.setField(caps, "mVideoCaps", videoCaps);
      } else {
        AudioCapabilities audioCaps = createDefaultAudioCapabilities(caps, mediaFormat);
        ReflectionHelpers.setField(caps, "mAudioCaps", audioCaps);
      }

      if (isEncoder) {
        EncoderCapabilities encoderCaps = createDefaultEncoderCapabilities(caps, mediaFormat);
        ReflectionHelpers.setField(caps, "mEncoderCaps", encoderCaps);
      }

      int flagsSupported = getSupportedFeatures(caps, mediaFormat);
      ReflectionHelpers.setField(caps, "mFlagsSupported", flagsSupported);

      return caps;
    }

    /** Create a default {@link AudioCapabilities} for a given {@link MediaFormat}. */
    private AudioCapabilities createDefaultAudioCapabilities(
        CodecCapabilities parent, MediaFormat mediaFormat) {
      return ReflectionHelpers.callStaticMethod(
          AudioCapabilities.class,
          "create",
          ClassParameter.from(MediaFormat.class, mediaFormat),
          ClassParameter.from(CodecCapabilities.class, parent));
    }

    /** Create a default {@link VideoCapabilities} for a given {@link MediaFormat}. */
    private VideoCapabilities createDefaultVideoCapabilities(
        CodecCapabilities parent, MediaFormat mediaFormat) {
      return ReflectionHelpers.callStaticMethod(
          VideoCapabilities.class,
          "create",
          ClassParameter.from(MediaFormat.class, mediaFormat),
          ClassParameter.from(CodecCapabilities.class, parent));
    }

    /** Create a default {@link EncoderCapabilities} for a given {@link MediaFormat}. */
    private EncoderCapabilities createDefaultEncoderCapabilities(
        CodecCapabilities parent, MediaFormat mediaFormat) {
      return ReflectionHelpers.callStaticMethod(
          EncoderCapabilities.class,
          "create",
          ClassParameter.from(MediaFormat.class, mediaFormat),
          ClassParameter.from(CodecCapabilities.class, parent));
    }

    /**
     * Extract codec features from a given {@link MediaFormat} and convert them to values recognized
     * by {@link CodecCapabilities}.
     */
    private int getSupportedFeatures(CodecCapabilities parent, MediaFormat mediaFormat) {
      int flagsSupported = 0;
      Object[] validFeatures = ReflectionHelpers.callInstanceMethod(parent, "getValidFeatures");
      for (Object validFeature : validFeatures) {
        String featureName = (String) ReflectionHelpers.getField(validFeature, "mName");
        int featureValue = (int) ReflectionHelpers.getField(validFeature, "mValue");
        if (mediaFormat.containsFeature(featureName)
            && mediaFormat.getFeatureEnabled(featureName)) {
          flagsSupported |= featureValue;
        }
      }
      return flagsSupported;
    }
  }
}
