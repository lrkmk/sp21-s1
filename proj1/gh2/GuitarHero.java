package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static GuitarString[] strings = new GuitarString[37];

    public static void main(String[] args) {

        for(int i = 0; i < strings.length; i++) {
            GuitarString stringA = new GuitarString(Math.pow(440.0 * 2, (i - 24.0) / 12));
            strings[i] = stringA;
        }

        while (true) {
            int outer_index = 0;
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index != -1) {
                    strings[index].pluck();
                    outer_index = index;
                } else {
                    continue;
                }
            }

            double sample = strings[outer_index].sample();
            StdAudio.play(sample);
            strings[outer_index].tic();
        }
    }
}
