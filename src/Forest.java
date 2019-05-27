import java.io.*;
import java.net.URL;
import java.util.ArrayList;

class Forest {

    /* Atributes */
    private String data;
    private ArrayList<Ranger> myRangers = new ArrayList<>();
    private ArrayList<Thread> myRangersThreads = new ArrayList<>();

    /* Constructors */
    private Forest() {}

    public Forest(Object obj) {
        data = obj.toString();
    }

    public Forest(String str) {
        data = str;
    }

    public Forest(File file) throws IOException {
        FileReader reader = new FileReader(file);
        StringBuilder str = new StringBuilder();
        int ch;

        while ((ch = reader.read()) != -1) str.append((char)ch);
        reader.close();

        data = str.toString();
    }

    public Forest(URL url) throws IOException {
        InputStreamReader reader = new InputStreamReader(url.openStream());
        StringBuilder str = new StringBuilder();
        int ch;

        while ((ch = reader.read()) != -1) str.append((char)ch);
        reader.close();

        data = str.toString();
    }

    public Forest(InputStream inStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inStream);
        StringBuilder str = new StringBuilder();
        int ch;

        while ((ch = reader.read()) != -1) str.append((char)ch);
        reader.close();

        data = str.toString();
    }

    /* Getters */
    public String getData() {
        return data;
    }

    /* Methods */
    public Forest addRanger(Ranger ... rans) {
        for (Ranger ran: rans) {
            ran.myForest = this;
            ran.setLimit(getData().length());
            myRangers.add(ran);
        }
        return this;
    }

    public void play() {
        Thread th;
        for (Ranger ran: myRangers) {
            if (ran.getName() == null)
                th = new Thread(ran);
            else
                th = new Thread(ran, ran.getName());

            myRangersThreads.add(th);
            th.start();
        }
    }

    public boolean areAlive() {
        for (Thread th: myRangersThreads) {
            if (th.isAlive())
                return true;
        }
        return false;
    }
}