package invalid;

import java.util.Date;

public class Godclass {

	public void bigMethod(final String vorname, final String nachname, final int alter, final Date bday,
	                      final String hobbies, final int zahl1, final int zahl2, final int zahl3, final int zahl4,
	                      final int zahl5, final int zahl6) {

		String vollerName = "";
		if (!vorname.isEmpty()) {
			if (!nachname.isEmpty()) {
				vollerName = vorname + " " + nachname;
			}
		}

		if (!vorname.isEmpty()) {
			if (!nachname.isEmpty()) {
				vollerName = vorname + " " + nachname;
			}
		}

		if (!vorname.isEmpty()) {
			if (!nachname.isEmpty()) {
				vollerName = vorname + " " + nachname;
			}
		}

		if (!vorname.isEmpty()) {
			if (!nachname.isEmpty()) {
				vollerName = vorname + " " + nachname;
			}
		}

		String nameAlter = "";
		if (!vollerName.isEmpty()) {
			nameAlter = vollerName + ", " + alter;
		}

		final DateFormat richtigerBday = new SimpleDateFormat("HH:mm:ss");

		String mehrDaten = "";
		if (!mehrDaten.isEmpty()) {
			mehrDaten = nameAlter + ", " + richtigerBday.format(bday);
		}

		String nochMehrDaten = "";
		if (!nochMehrDaten.isEmpty()) {
			nochMehrDaten = mehrDaten + ", \n" + hobbies;
		}
	}
}
