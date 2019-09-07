package net.tiny.resume;

public interface Version {

    interface CodeEnum<E extends Enum<E>> {
        String ver();
        int major();
        int minor();
        @SuppressWarnings("unchecked")
        default E toEnum() {
            return (E) this;
        }
    }

    enum Code implements CodeEnum<Code> {
        VERSION_0_9(0.9f),
        VERSION_0_91(0.91f),
        VERSION_1_0(1.0f),
        VERSION_1_1(1.1f);

        private String ver;
        private int major;
        private int minor;
        Code(float value) {
            this.ver = Float.toString(value);
            int pos = ver.indexOf(".");
            this.major = Integer.parseInt(ver.substring(0, pos));
            this.minor = Integer.parseInt(ver.substring(pos+1));
        }

        @Override
        public String ver() {
            return ver;
        }

        @Override
        public int major() {
            return major;
        }

        @Override
        public int minor() {
            return minor;
        }

    }

    Code CURRENT = Code.VERSION_0_9;
}
