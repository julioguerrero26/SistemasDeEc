import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static final String RESET = "\033[0m";
    static final String BOLD = "\033[1m";
    static final String RED = "\033[31m";
    static final String GREEN = "\033[32m";
    static final String YELLOW = "\033[33m";
    static final String CYAN = "\033[36m";
    static final String CLEAR = "\033[H\033[2J";

    static class Ecuacion {
        int a, b, c;
        char v1, v2;
        int p1, p2;
        boolean valida;
    }

    static Ecuacion analizarEcuacion(String strEc) {
        Ecuacion ec = new Ecuacion();
        ec.valida = false;
        try {
            String[] partes = strEc.split("=");
            if (partes.length != 2) return ec;

            // Limpiamos espacios en blanco
            String izq = partes[0].replaceAll("\\s+", "");
            String der = partes[1].replaceAll("\\s+", "");

            int c_total = Integer.parseInt(der);

            Pattern pBinomio = Pattern.compile("([+-]?\\d*)\\(([a-zA-Z])([+-]\\d+)\\)");
            Matcher mBinomio = pBinomio.matcher(izq);
            StringBuffer sb = new StringBuffer();

            while (mBinomio.find()) {
                String strCoefExt = mBinomio.group(1);
                String var = mBinomio.group(2);
                String strConstInt = mBinomio.group(3);

                int coefExt = 1;
                if (strCoefExt.equals("-")) coefExt = -1;
                else if (strCoefExt.equals("+") || strCoefExt.isEmpty()) coefExt = 1;
                else coefExt = Integer.parseInt(strCoefExt.replace("+", ""));

                int constInt = Integer.parseInt(strConstInt.replace("+", ""));

                int nuevoCoefVar = coefExt;
                int nuevaConstante = coefExt * constInt;

                String reemplazo = (nuevoCoefVar >= 0 ? "+" : "") + nuevoCoefVar + var +
                        (nuevaConstante >= 0 ? "+" : "") + nuevaConstante;
                mBinomio.appendReplacement(sb, reemplazo);
            }
            mBinomio.appendTail(sb);
            izq = sb.toString();

            Pattern pTermino = Pattern.compile("([+-]?\\d*)([a-zA-Z])(?:\\^(\\d+))?");
            Matcher mRemocion = pTermino.matcher(izq);
            String soloConstantes = mRemocion.replaceAll("");

            int constantesIzquierda = 0;
            Pattern pConst = Pattern.compile("[+-]?\\d+");
            Matcher mConst = pConst.matcher(soloConstantes);
            while (mConst.find()) {
                constantesIzquierda += Integer.parseInt(mConst.group().replace("+", ""));
            }

            ec.c = c_total - constantesIzquierda;

            Matcher mTermino = pTermino.matcher(izq);
            int contador = 0;

            while (mTermino.find()) {
                String strCoef = mTermino.group(1);
                char var = mTermino.group(2).charAt(0);
                String strPot = mTermino.group(3);

                int coef = 1;
                if (strCoef.equals("-")) coef = -1;
                else if (!strCoef.isEmpty() && !strCoef.equals("+")) {
                    coef = Integer.parseInt(strCoef.replace("+", ""));
                }

                int pot = 1;
                if (strPot != null && !strPot.isEmpty()) pot = Integer.parseInt(strPot);

                if (contador == 0) {
                    ec.a = coef; ec.v1 = var; ec.p1 = pot;
                } else if (contador == 1) {
                    ec.b = coef; ec.v2 = var; ec.p2 = pot;
                }
                contador++;
            }

            if (contador == 2) ec.valida = true;

        } catch (Exception ignore) {
        }
        return ec;
    }

    static void sistemaDeEcuacion() {
        boolean datosConfirmados = false;

        while (!datosConfirmados) {
            System.out.print(CLEAR);
            System.out.flush();

            System.out.println(BOLD + CYAN + "<h> Calculadora de Sistemas 2x2 </h>" + RESET);

            Ecuacion ec1 = null;
            Ecuacion ec2 = null;

            while (true) {
                String str1 = IO.leerLinea(YELLOW + "Ingresa la primera ecuación (ej. 2(x+3) - 3y = 15): " + RESET);
                ec1 = analizarEcuacion(str1);
                if (ec1.valida) break;
                System.out.println(RED + "¡Error! Ingresa una ecuación válida." + RESET);
            }

            while (true) {
                String str2 = IO.leerLinea(YELLOW + "Ingresa la segunda ecuación (ej. 3(x-2) + 2(y+4) = 16): " + RESET);
                ec2 = analizarEcuacion(str2);
                if (ec2.valida) {
                    if ((ec1.v1 == ec2.v1 && ec1.p1 == ec2.p1 && ec1.v2 == ec2.v2 && ec1.p2 == ec2.p2) ||
                            (ec1.v1 == ec2.v2 && ec1.p1 == ec2.p2 && ec1.v2 == ec2.v1 && ec1.p2 == ec2.p1)) {

                        if (ec1.v1 != ec2.v1 || ec1.p1 != ec2.p1) {
                            int tempA = ec2.a; ec2.a = ec2.b; ec2.b = tempA;
                            char tempV = ec2.v1; ec2.v1 = ec2.v2; ec2.v2 = tempV;
                            int tempP = ec2.p1; ec2.p1 = ec2.p2; ec2.p2 = tempP;
                        }
                        break;
                    } else {
                        System.out.println(RED + "¡Error! Las variables y potencias deben ser consistentes." + RESET);
                    }
                } else {
                    System.out.println(RED + "¡Error! Ingresa una ecuación válida." + RESET);
                }
            }

            int a1 = ec1.a, b1 = ec1.b, c1 = ec1.c;
            int a2 = ec2.a, b2 = ec2.b, c2 = ec2.c;
            char v1 = ec1.v1, v2 = ec1.v2;
            String p1Str = ec1.p1 > 1 ? "^" + ec1.p1 : "";
            String p2Str = ec1.p2 > 1 ? "^" + ec1.p2 : "";

            String signoB1 = (b1 < 0) ? "-" : "+";
            String signoB2 = (b2 < 0) ? "-" : "+";

            System.out.printf("""

                    %s%sTu sistema de ecuaciones (simplificado) es:%s
                    (1) %d%c%s %s %d%c%s = %d
                    (2) %d%c%s %s %d%c%s = %d
                    """,
                    BOLD, CYAN, RESET,
                    a1, v1, p1Str, signoB1, Math.abs(b1), v2, p2Str, c1,
                    a2, v1, p1Str, signoB2, Math.abs(b2), v2, p2Str, c2);

            String confirmacion = IO.leerLinea("\n" + YELLOW + "¿Está correcto? (s/n): " + RESET);
            boolean respuesta = confirmacion.equalsIgnoreCase("s");

            if (respuesta) {
                datosConfirmados = true;
                String metodo = IO.leerLinea(
                        "\n" + YELLOW + "¿Por qué método resolver? ([S]Sustitución/[I]Igualación): " + RESET);

                if (metodo.equalsIgnoreCase("S")) {
                    resolverPorSustitucion(a1, b1, c1, a2, b2, c2, v1, p1Str, v2, p2Str);
                } else if (metodo.equalsIgnoreCase("I")) {
                    resolverPorIgualacion(a1, b1, c1, a2, b2, c2, v1, p1Str, v2, p2Str);
                } else {
                    System.out.println(RED + "Método no reconocido." + RESET);
                }
            } else {
                System.out.println("\n" + RED + "Reintentando datos de entrada..." + RESET);
                try { Thread.sleep(1500); } catch (InterruptedException ignore) {}
            }
        }
    }

    static void resolverPorSustitucion(int a1, int b1, int c1, int a2, int b2, int c2, char v1, String p1Str, char v2, String p2Str) {
        double determinanteDet = a1 * b2 - a2 * b1;
        if (determinanteDet != 0) {
            double x = (double) (c1 * b2 - c2 * b1) / determinanteDet;
            double y = (double) (a1 * c2 - a2 * c1) / determinanteDet;
            System.out.printf("\n%s[Sustitución] Solución encontrada:%s\n %c%s = %.3f\n %c%s = %.3f\n", GREEN, RESET, v1, p1Str, x, v2, p2Str, y);
        } else {
            System.out.println("\n" + RED + "El sistema no tiene solución única (Determinante = 0)." + RESET);
        }
    }

    static void resolverPorIgualacion(int a1, int b1, int c1, int a2, int b2, int c2, char v1, String p1Str, char v2, String p2Str) {
        double det = (a1 * b2) - (a2 * b1);
        if (det != 0) {
            double y = (double) (a1 * c2 - a2 * c1) / det;
            double x = (a1 != 0) ? (c1 - b1 * y) / a1 : (c2 - b2 * y) / a2;
            System.out.printf("\n%s[Igualación] Solución encontrada:%s\n %c%s = %.3f\n %c%s = %.3f\n", GREEN, RESET, v1, p1Str, x, v2, p2Str, y);
        } else {
            System.out.println("\n" + RED + "El sistema es inconsistente o dependiente." + RESET);
        }
    }

    public static void main(String[] args) { sistemaDeEcuacion(); }
}

class IO {
    private static final Scanner scanner = new Scanner(System.in);
    static String leerLinea(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}