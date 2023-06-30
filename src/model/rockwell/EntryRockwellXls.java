package model.rockwell;

import model.EntryXlsx;

public class EntryRockwellXls extends EntryXlsx {

	private String fIndice;

	public EntryRockwellXls(String fIndice, String fTagNameSCADA, String fAddrSymPlc, String fTipo, int fSviluppo,
			String fSigla, String fDescrizione, String fDescrizioneEstesa, String fLimiteMIN, String fLimiteMAX,
			String fUM, String fUsoDelBit, String fCommento, String fLivello, String fUpdated, boolean flag_print) {
		super("", "", "", fAddrSymPlc, "none", fTagNameSCADA, fTipo, fSviluppo, fSigla, fDescrizione,
				fDescrizioneEstesa, fLimiteMIN, fLimiteMAX, fUM, fUsoDelBit, fCommento, fLivello, fUpdated, flag_print);
		this.fIndice = fIndice;
		this.fDescrizioneEstesa = fDescrizioneEstesa;
	}

	public String getfIndice() {
		return fIndice;
	}

	public void setfIndice(String fIndice) {
		this.fIndice = fIndice;
	}

}
