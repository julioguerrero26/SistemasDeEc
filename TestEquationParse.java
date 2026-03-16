import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestEquationParse {
    static class Equation {
        int a, b, c;
        char var1, var2;
        boolean valid;

        public String toString() {
            return valid ? (a + "" + var1 + (b >= 0 ? "+" : "") + b + "" + var2 + "=" + c) : "invalid";
        }
    }

    static Equation parseEquation(String eqStr) {
        Equation eq = new Equation();
        eq.valid = false;
        try {
            String[] parts = eqStr.split("=");
            if (parts.length != 2)
                return eq;

            String left = parts[0].replaceAll("\\s+", "");
            String right = parts[1].replaceAll("\\s+", "");

            eq.c = Integer.parseInt(right);

            Pattern p = Pattern.compile("([+-]?\\d*)([a-zA-Z])");
            Matcher m = p.matcher(left);

            int count = 0;
            int lastEnd = 0;
            while (m.find()) {
                if (m.start() != lastEnd)
                    return eq;
                lastEnd = m.end();
                String coeffStr = m.group(1);
                char var = m.group(2).charAt(0);
                int coeff = 1;
                if (coeffStr.equals("-"))
                    coeff = -1;
                else if (!coeffStr.isEmpty() && !coeffStr.equals("+")) {
                    coeff = Integer.parseInt(coeffStr.replace("+", ""));
                }

                if (count == 0) {
                    eq.a = coeff;
                    eq.var1 = var;
                } else if (count == 1) {
                    eq.b = coeff;
                    eq.var2 = var;
                }
                count++;
            }
            if (lastEnd != left.length() || count != 2)
                return eq;
            eq.valid = true;
        } catch (Exception e) {
        }
        return eq;
    }

    public static void main(String[] args) {
        System.out.println(parseEquation("2z + 3w = 5"));
        System.out.println(parseEquation("z - w = -10"));
        System.out.println(parseEquation("-z + w = 0"));
        System.out.println(parseEquation("10x - y = +5"));
        System.out.println(parseEquation("2x - 3y + z = 1"));
        System.out.println(parseEquation("2x = 3"));
    }
}
