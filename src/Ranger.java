public abstract class Ranger implements Runnable {

    /* Atributes */
    private String name;
    private int ix;
    private int limit = 5;
    public Forest myForest;

    /* Constructors */
    public Ranger() {}

    public Ranger(String name) {
        this.name = name;
    }

    /* Getters */
    public int getIx() {
        return ix;
    }

    public String getName() {
        return name;
    }

    public int getLimit() {
        return limit;
    }

    /* Setters */
    public boolean setLimit(int limit) {
        if (canIgo(limit-1) && limit > ix) {
            this.limit = limit;
            return true;
        }
        else return false;
    }

    /* Methods */
    private boolean canIgo(int pos) {
        try {
            myForest.getData().charAt(pos);
            return true;
        }
        catch (StringIndexOutOfBoundsException e) {
            return false;
        }
    }

    public boolean go(int pos) {
        if (canIgo(pos)) {
            ix = pos;
            return true;
        }
        else return false;
    }

    public boolean skip() {
        if (canIgo(ix)) {
            ix++;
            return true;
        }
        else return false;
    }

    public boolean skip(int howMany) {
        if (canIgo(ix+howMany)) {
            ix += howMany;
            return true;
        }
        else return false;
    }

    public char read() {
        return canIgo(ix) ? myForest.getData().charAt(ix++) : (char)-1;
    }

    public String read(int howMany) {
        if (canIgo(ix+howMany-1)) {
            String str = myForest.getData().substring(ix, ix+howMany);
            ix += howMany;
            return str;
        }
        else return null;
    }

    /* -- ghosts */
    private int goToPatternGhost(String pat, int start, int end, boolean before) { // start inclusive, end exclusive
        boolean found = false;
        int i, patIx = 0;
        char ch;

        for (i=start; i < end; i++) {

            ch = myForest.getData().charAt(i);

            if (ch == pat.charAt(patIx)) {
                if (patIx == pat.length()-1) {
                    found = true;
                    break;
                }
                patIx++;
            }
            else if (ch == pat.charAt(0)) patIx = 1;
            else patIx = 0;
        }

        if (found) return before ? i-pat.length() : i+1;
        else return -1;
    }

    public int goBeforeGhost(String pat, int start, int end) {
        return goToPatternGhost(pat, start, end, true);
    }
    public int goBeforeGhost(String pat, int start) {
        return goToPatternGhost(pat, start, limit, true);
    }
    public int goBeforeGhost(String pat) {
        return goToPatternGhost(pat, ix, limit, true);
    }

    public int goAfterGhost(String pat, int start, int end) {
        return goToPatternGhost(pat, start, end, false);
    }
    public int goAfterGhost(String pat, int start) {
        return goToPatternGhost(pat, start, limit, false);
    }
    public int goAfterGhost(String pat) {
        return goToPatternGhost(pat, ix, limit, false);
    }

    public String getUpToGhost(String pat) {
        int ix2 = goBeforeGhost(pat);

        StringBuilder str = new StringBuilder();
        for (int i=ix; i <= ix2; i++)
            str.append(myForest.getData().charAt(i));

        return str.toString();
    }

    /* -- alives */
    private boolean goToPattern(String pat, int start, int end, boolean before) { // start inclusive, end exclusive
        int ixTemp = goToPatternGhost(pat, start, end, before);
        if (ixTemp != -1) {
            ix = ixTemp;
            return true;
        }
        else return false;
    }

    public boolean goBefore(String pat, int start, int end) {
        return goToPattern(pat, start, end, true);
    }
    public boolean goBefore(String pat, int start) {
        return goToPattern(pat, start, limit, true);
    }
    public boolean goBefore(String pat) {
        return goToPattern(pat, ix, limit, true);
    }

    public boolean goAfter(String pat, int start, int end) {
        return goToPattern(pat, start, end, false);
    }
    public boolean goAfter(String pat, int start) {
        return goToPattern(pat, start, limit, false);
    }
    public boolean goAfter(String pat) {
        return goToPattern(pat, ix, limit, false);
    }

    public String getUpTo(String pat) {
        int ix2 = goBeforeGhost(pat);

        StringBuilder str = new StringBuilder();
        for (int i=ix; i <= ix2; i++)
            str.append(myForest.getData().charAt(i));

        ix = ix2+1;
        return str.toString();
    }
}