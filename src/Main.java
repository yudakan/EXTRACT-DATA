import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {

        // Create forest for internet data
        final Forest[] html = new Forest[1];

        Thread th = new Thread(() -> {
            try {
                html[0] = new Forest(new URL("http://www.bolsamadrid.es/esp/aspx/Mercados/Precios.aspx?indice=ESI100000000"));
            }
            catch (IOException e) {
                html[0] = null;
            }
        });
        th.start();
        long t0 = System.nanoTime();
        long delta = 0;

        System.out.print("     Downloading data...");

        while (th.isAlive() && delta < 5_000_000_000L) {
            if (delta % 10_000_000 == 0)
                System.out.print('.');

            delta = System.nanoTime()-t0;
        }
        System.out.println();

        if (html[0] == null) {
            System.out.println("     Something went wrong -_-");
            System.exit(1);
        }

        // Create one ranger to get html table data
        final ArrayList<Empresa> list = new ArrayList<>();

        Ranger ranTaula = new Ranger() {
            @Override
            public void run() {

                goAfter("ctl00_Contenido_tblAcciones");
                setLimit(goBeforeGhost("</table>"));

                while (goBefore("<tr")) {

                    goAfter("<td");
                    goAfter(">");
                    goAfter(">");
                    String name = getUpTo("</");

                    goAfter("</td>");
                    goAfter("</td>");
                    goAfter("</td>");
                    goAfter("</td>");
                    goAfter("</td><td>");
                    int volume = Integer.parseInt(getUpTo("</").replace(".", ""));

                    list.add(new Empresa(name, volume) {
                        @Override
                        public String toString() {
                            String space = "               ";
                            return getName() + space.substring(0, space.length()-getName().length()) + getVolume();
                        }
                    });
                }
            }
        };

        // Create another ranger to get footer
        final StringBuilder footer = new StringBuilder();

        Ranger ranFooter = new Ranger() {
            @Override
            public void run() {

                goAfter("id=\"PieOps\"");
                setLimit(goBeforeGhost("</div>"));

                while (goAfter(">&nbsp;")) {

                    footer.append(getUpTo("&nbsp;<"));
                    footer.append(" - ");
                }
                footer.delete(footer.length()-3, footer.length());
            }
        };

        // Add ranger in the forest & play adventure
        html[0].addRanger(ranTaula, ranFooter).play();
        t0 = System.nanoTime();
        delta = 0;

        System.out.print("     Exploring the territory...");

        while (html[0].areAlive() && delta < 5_000_000_000L) {
            if (delta % 10_000_000 == 0)
                System.out.print('.');

            delta = System.nanoTime()-t0;
        }
        System.out.println();

        // Sort by volume
        System.out.println("     Sorting...");
        Collections.sort(list, (e1, e2) -> Integer.compare(e2.getVolume(), e1.getVolume()));

        // Create .txt
        File txt = new File("./ibex35.txt");
        System.out.println("     Writing "+txt.getName());

        try {
            PrintWriter writer = new PrintWriter(txt);
            for (Empresa emp: list)
                writer.println(emp);

            writer.println('\n' + "Footer: " + footer.toString());

            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            System.out.println("     Something went wrong -_-");
        }

        System.out.println("     Done! ^-^");
    }
}

class Empresa implements Comparable<Empresa> {

    /* Atributes */
    private String name;
    private int volume;

    /* Constructor */
    public Empresa(String name, int volume) {
        this.name = name;
        this.volume = volume;
    }

    /* Getters */
    public String getName() {
        return name;
    }

    public int getVolume() {
        return volume;
    }

    /* Setters */
    public void setName(String name) {
        this.name = name;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    /* Methods */
    @Override
    public int compareTo(Empresa empresa) {
        return this.name.compareTo(empresa.getName());
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "name='" + name + '\'' +
                ", volume='" + volume + '\'' +
                '}';
    }
}