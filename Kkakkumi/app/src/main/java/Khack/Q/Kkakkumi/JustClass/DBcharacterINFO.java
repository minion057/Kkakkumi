package Khack.Q.Kkakkumi.JustClass;

import android.provider.BaseColumns;

public class DBcharacterINFO {
    private DBcharacterINFO(){}
    public static class DBinfo implements BaseColumns{
        public static final String TABLE_NAME = "character";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_VISIBLE = "visible";
    }
}
