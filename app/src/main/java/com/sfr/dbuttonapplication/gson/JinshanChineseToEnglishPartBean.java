package com.sfr.dbuttonapplication.gson;

import java.util.List;

/**
 * Created by ASUS on 2018/8/24.
 */

public class JinshanChineseToEnglishPartBean {

    /**
     * word_id : 2143763
     * word_name : 好
     * symbols : [{"symbol_id":"2144972","word_id":"2143763","word_symbol":"hǎo","symbol_mp3":"http://res.iciba.com/hanyu/zi/19a73d32bf61c88bc4ed86c40f26bc9c.mp3","parts":[{"part_name":"形","means":[{"mean_id":"2465987","part_id":"2148468","word_mean":"good","has_mean":"1","split":1},{"mean_id":"2465988","part_id":"2148468","word_mean":"fine","has_mean":"1","split":1},{"mean_id":"2465989","part_id":"2148468","word_mean":"nice","has_mean":"1","split":0}]}],"ph_am_mp3":"","ph_en_mp3":"","ph_tts_mp3":"","ph_other":""},{"symbol_id":"2144973","word_id":"2143763","word_symbol":"hào","symbol_mp3":"","parts":[{"part_name":"动","means":[{"mean_id":"2465990","part_id":"2148469","word_mean":"like","has_mean":"1","split":1},{"mean_id":"2465991","part_id":"2148469","word_mean":"love","has_mean":"1","split":1},{"mean_id":"2465992","part_id":"2148469","word_mean":"be fond of","has_mean":"1","split":0}]},{"part_name":"名","means":[{"mean_id":"2465993","part_id":"2148470","word_mean":"a surname","has_mean":"0","split":0}]}]}]
     */

    private String word_id;
    private String word_name;
    private List<SymbolsBean> symbols;

    public String getWord_id() {
        return word_id;
    }

    public void setWord_id(String word_id) {
        this.word_id = word_id;
    }

    public String getWord_name() {
        return word_name;
    }

    public void setWord_name(String word_name) {
        this.word_name = word_name;
    }

    public List<SymbolsBean> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<SymbolsBean> symbols) {
        this.symbols = symbols;
    }

    public static class SymbolsBean {
        /**
         * symbol_id : 2144972
         * word_id : 2143763
         * word_symbol : hǎo
         * symbol_mp3 : http://res.iciba.com/hanyu/zi/19a73d32bf61c88bc4ed86c40f26bc9c.mp3
         * parts : [{"part_name":"形","means":[{"mean_id":"2465987","part_id":"2148468","word_mean":"good","has_mean":"1","split":1},{"mean_id":"2465988","part_id":"2148468","word_mean":"fine","has_mean":"1","split":1},{"mean_id":"2465989","part_id":"2148468","word_mean":"nice","has_mean":"1","split":0}]}]
         * ph_am_mp3 :
         * ph_en_mp3 :
         * ph_tts_mp3 :
         * ph_other :
         */

        private String symbol_id;
        private String word_id;
        private String word_symbol;
        private String symbol_mp3;
        private String ph_am_mp3;
        private String ph_en_mp3;
        private String ph_tts_mp3;
        private String ph_other;
        private List<PartsBean> parts;

        public String getSymbol_id() {
            return symbol_id;
        }

        public void setSymbol_id(String symbol_id) {
            this.symbol_id = symbol_id;
        }

        public String getWord_id() {
            return word_id;
        }

        public void setWord_id(String word_id) {
            this.word_id = word_id;
        }

        public String getWord_symbol() {
            return word_symbol;
        }

        public void setWord_symbol(String word_symbol) {
            this.word_symbol = word_symbol;
        }

        public String getSymbol_mp3() {
            return symbol_mp3;
        }

        public void setSymbol_mp3(String symbol_mp3) {
            this.symbol_mp3 = symbol_mp3;
        }

        public String getPh_am_mp3() {
            return ph_am_mp3;
        }

        public void setPh_am_mp3(String ph_am_mp3) {
            this.ph_am_mp3 = ph_am_mp3;
        }

        public String getPh_en_mp3() {
            return ph_en_mp3;
        }

        public void setPh_en_mp3(String ph_en_mp3) {
            this.ph_en_mp3 = ph_en_mp3;
        }

        public String getPh_tts_mp3() {
            return ph_tts_mp3;
        }

        public void setPh_tts_mp3(String ph_tts_mp3) {
            this.ph_tts_mp3 = ph_tts_mp3;
        }

        public String getPh_other() {
            return ph_other;
        }

        public void setPh_other(String ph_other) {
            this.ph_other = ph_other;
        }

        public List<PartsBean> getParts() {
            return parts;
        }

        public void setParts(List<PartsBean> parts) {
            this.parts = parts;
        }

        public static class PartsBean {
            /**
             * part_name : 形
             * means : [{"mean_id":"2465987","part_id":"2148468","word_mean":"good","has_mean":"1","split":1},{"mean_id":"2465988","part_id":"2148468","word_mean":"fine","has_mean":"1","split":1},{"mean_id":"2465989","part_id":"2148468","word_mean":"nice","has_mean":"1","split":0}]
             */

            private String part_name;
            private List<MeansBean> means;

            public String getPart_name() {
                return part_name;
            }

            public void setPart_name(String part_name) {
                this.part_name = part_name;
            }

            public List<MeansBean> getMeans() {
                return means;
            }

            public void setMeans(List<MeansBean> means) {
                this.means = means;
            }

            public static class MeansBean {
                /**
                 * mean_id : 2465987
                 * part_id : 2148468
                 * word_mean : good
                 * has_mean : 1
                 * split : 1
                 */

                private String mean_id;
                private String part_id;
                private String word_mean;
                private String has_mean;
                private int split;

                public String getMean_id() {
                    return mean_id;
                }

                public void setMean_id(String mean_id) {
                    this.mean_id = mean_id;
                }

                public String getPart_id() {
                    return part_id;
                }

                public void setPart_id(String part_id) {
                    this.part_id = part_id;
                }

                public String getWord_mean() {
                    return word_mean;
                }

                public void setWord_mean(String word_mean) {
                    this.word_mean = word_mean;
                }

                public String getHas_mean() {
                    return has_mean;
                }

                public void setHas_mean(String has_mean) {
                    this.has_mean = has_mean;
                }

                public int getSplit() {
                    return split;
                }

                public void setSplit(int split) {
                    this.split = split;
                }
            }
        }
    }
}
