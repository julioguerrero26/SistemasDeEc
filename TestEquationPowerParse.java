import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestEquationPowerParse {
    static class Ecuacion {
        int a, b, c;
        char v1, v2;
        int p1, p2;
        boolean valida;

        public String toString() {
            return valida ? (a + "" + v1 + "^" + p1 + (b >= 0 ? "+" : "") + b + "" + v2 + "^" + p2 + "=" + c)
                    : "invalid";
        }
    }

    static Ecuacion analizarEcuacion(String strEc) {
        Ecuacion ec = new Ecuacion();
        ec.valida = false;
        try {
            String[] partes = strEc.split("=");
            if (partes.length != 2)
                return ec;

            String izq = partes[0].replaceAll("\\s+", "");
            String der = partes[1].replaceAll("\\s+", "");

            ec.c = Integer.parseInt(der);

            Pattern p = Pattern.compile("([+-]?\\d*)([a-zA-Z])(?:\\^(\\d+))?");
            Matcher m = p.matcher(izq);

            int contador = 0;
            int ultimoFin = 0;
            while (m.find()) {
                if (m.start() != ultimoFin)
                    return ec;
                ultimoFin = m.end();
                String strCoef = m.group(1);
                char var = m.group(2).charAt(0);
                String strPot = m.group(3);

                int coef = 1;
                if (strCoef.equals("-"))
                    coef = -1;
                else if (!strCoef.isEmpty() && !strCoef.equals("+")) {
                    coef = Integer.parseInt(strCoef.replace("+", ""));
                }

                int pot = 1;
                if (strPot != null && !strPot.isEmpty()) {
                    pot = Integer.parseInt(strPot);
                }

                if (contador == 0) {
                    ec.a = coef;
                    ec.v1 = var;
                    ec.p1 = pot;
                } else if (contador == 1) {
                    ec.b = coef;
                    ec.v2 = var;
                    ec.p2 = pot;
                }
                contador++;
            }
            if (ultimoFin != izq.length() || contador != 2)
                return ec;

            ec.valida = true;
        } catch (Exception e) {
        }
        return ec;
    }

    public static void main(String[] args) {
        System.out.println(analizarEcuacion("2z^2 + 3w = 5"));
        System.out.println(analizarEcuacion("z - w^3 = -10"));
        System.out.println(analizarEcuacion("-z^5 + w^2 = 0"));
        System.out.println(analizarEcuacion("10x - y = +5"));
        System.out.println(analizarEcuacion("2x - 3y + z = 1"));
        System.out.println(analizarEcuacion("2x = 3"));
    }
}
