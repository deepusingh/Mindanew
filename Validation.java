package maslsalesapp.minda.miscellaneousclasses;

public class Validation {

    public static boolean validateFirstname(String firstrname) {
        if (firstrname.length() >= 1) {
            return true;
        } else {
            return false;
        }
    }

    public final static boolean ValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static boolean minimumlengthchangepaswd(String username) {
        if (username.length() > 5) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validatePassword(String password) {
        if (password.length() >= 1) {
            return true;
        } else {
            return false;
        }
    }


}
