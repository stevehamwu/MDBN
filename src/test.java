import java.text.DecimalFormat;

public class test {
    public static void main(String[] args) {
        String[][] str1 = {{"a", "1"}, {"b", "2"}};
        String[] str2 = {"c", "3"};
        String[][] str = new String[str1.length+1][2];
        System.arraycopy(str1, 0, str, 0, str1.length);
        System.arraycopy(str2, 0, str[str.length-1], 0, 2);
        for (int i = 0; i < str.length; i++) {
            for (int j = 0; j < str[i].length; j++) {
                System.out.print(str[i][j] + " ");
            }
            System.out.println();
        }

        DecimalFormat df = new DecimalFormat("#.000");
        double d = 0.8577443124069449;
        d = Double.valueOf(df.format(d));
        System.out.println(d);
    }
}
