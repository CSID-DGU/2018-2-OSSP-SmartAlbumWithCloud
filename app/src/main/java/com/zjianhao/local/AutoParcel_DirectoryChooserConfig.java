//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zjianhao.local;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.BitSet;

final class AutoParcel_DirectoryChooserConfig extends com.zjianhao.local.DirectoryChooserConfig {
    private final String newDirectoryName;
    private final String initialDirectory;
    private final boolean allowReadOnlyDirectory;
    private final boolean allowNewDirectoryNameModification;
    public static final Parcelable.Creator<AutoParcel_DirectoryChooserConfig> CREATOR = new Parcelable.Creator<AutoParcel_DirectoryChooserConfig>() {
        public AutoParcel_DirectoryChooserConfig createFromParcel(Parcel in) {
            return new AutoParcel_DirectoryChooserConfig(in);
        }

        public AutoParcel_DirectoryChooserConfig[] newArray(int size) {
            return new AutoParcel_DirectoryChooserConfig[size];
        }
    };
    private static final ClassLoader CL = AutoParcel_DirectoryChooserConfig.class.getClassLoader();

    private AutoParcel_DirectoryChooserConfig(String newDirectoryName, String initialDirectory, boolean allowReadOnlyDirectory, boolean allowNewDirectoryNameModification) {
        if(newDirectoryName == null) {
            throw new NullPointerException("Null newDirectoryName");
        } else {
            this.newDirectoryName = newDirectoryName;
            if(initialDirectory == null) {
                throw new NullPointerException("Null initialDirectory");
            } else {
                this.initialDirectory = initialDirectory;
                this.allowReadOnlyDirectory = allowReadOnlyDirectory;
                this.allowNewDirectoryNameModification = allowNewDirectoryNameModification;
            }
        }
    }

    String newDirectoryName() {
        return this.newDirectoryName;
    }

    String initialDirectory() {
        return this.initialDirectory;
    }

    boolean allowReadOnlyDirectory() {
        return this.allowReadOnlyDirectory;
    }

    boolean allowNewDirectoryNameModification() {
        return this.allowNewDirectoryNameModification;
    }

    public String toString() {
        return "DirectoryChooserConfig{newDirectoryName=" + this.newDirectoryName + ", " + "initialDirectory=" + this.initialDirectory + ", " + "allowReadOnlyDirectory=" + this.allowReadOnlyDirectory + ", " + "allowNewDirectoryNameModification=" + this.allowNewDirectoryNameModification + "}";
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        } else if(!(o instanceof DirectoryChooserConfig)) {
            return false;
        } else {
            DirectoryChooserConfig that = (DirectoryChooserConfig)o;
            return this.newDirectoryName.equals(that.newDirectoryName()) && this.initialDirectory.equals(that.initialDirectory()) && this.allowReadOnlyDirectory == that.allowReadOnlyDirectory() && this.allowNewDirectoryNameModification == that.allowNewDirectoryNameModification();
        }
    }

    public int hashCode() {
        int h = 1;
         h = h * 1000003;
        h ^= this.newDirectoryName.hashCode();
        h *= 1000003;
        h ^= this.initialDirectory.hashCode();
        h *= 1000003;
        h ^= this.allowReadOnlyDirectory?1231:1237;
        h *= 1000003;
        h ^= this.allowNewDirectoryNameModification?1231:1237;
        return h;
    }

    private AutoParcel_DirectoryChooserConfig(Parcel in) {
        this((String)in.readValue(CL), (String)in.readValue(CL), ((Boolean)in.readValue(CL)).booleanValue(), ((Boolean)in.readValue(CL)).booleanValue());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.newDirectoryName);
        dest.writeValue(this.initialDirectory);
        dest.writeValue(Boolean.valueOf(this.allowReadOnlyDirectory));
        dest.writeValue(Boolean.valueOf(this.allowNewDirectoryNameModification));
    }

    public int describeContents() {
        return 0;
    }

    static final class Builder extends DirectoryChooserConfig.Builder {
        private final BitSet set$ = new BitSet();
        private String newDirectoryName;
        private String initialDirectory;
        private boolean allowReadOnlyDirectory;
        private boolean allowNewDirectoryNameModification;

        Builder() {
        }

        Builder(DirectoryChooserConfig source) {
            this.newDirectoryName(source.newDirectoryName());
            this.initialDirectory(source.initialDirectory());
            this.allowReadOnlyDirectory(source.allowReadOnlyDirectory());
            this.allowNewDirectoryNameModification(source.allowNewDirectoryNameModification());
        }

        public DirectoryChooserConfig.Builder newDirectoryName(String newDirectoryName) {
            this.newDirectoryName = newDirectoryName;
            this.set$.set(0);
            return this;
        }

        public DirectoryChooserConfig.Builder initialDirectory(String initialDirectory) {
            this.initialDirectory = initialDirectory;
            this.set$.set(1);
            return this;
        }

        public DirectoryChooserConfig.Builder allowReadOnlyDirectory(boolean allowReadOnlyDirectory) {
            this.allowReadOnlyDirectory = allowReadOnlyDirectory;
            this.set$.set(2);
            return this;
        }

        public DirectoryChooserConfig.Builder allowNewDirectoryNameModification(boolean allowNewDirectoryNameModification) {
            this.allowNewDirectoryNameModification = allowNewDirectoryNameModification;
            this.set$.set(3);
            return this;
        }

        public DirectoryChooserConfig build() {
            if(this.set$.cardinality() < 4) {
                String[] propertyNames = new String[]{"newDirectoryName", "initialDirectory", "allowReadOnlyDirectory", "allowNewDirectoryNameModification"};
                StringBuilder missing = new StringBuilder();

                for(int i = 0; i < 4; ++i) {
                    if(!this.set$.get(i)) {
                        missing.append(' ').append(propertyNames[i]);
                    }
                }

                throw new IllegalStateException("Missing required properties:" + missing);
            } else {
                DirectoryChooserConfig result = new AutoParcel_DirectoryChooserConfig(this.newDirectoryName, this.initialDirectory, this.allowReadOnlyDirectory, this.allowNewDirectoryNameModification);
                return result;
            }
        }
    }
}
