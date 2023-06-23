package model.rockwell;

import model.EntryXlsx;

public class EntryRockwellXls extends EntryXlsx {
	
	private String fIndice;
	
	public EntryRockwellXls(String fIndice, String fTagNameSCADA, String fAddrSymPlc, String fTipo, int fSviluppo, String fSigla,
			String fDescrizione, String fDescrizioneEstesa, String fLimiteMIN, String fLimiteMAX, String fUM,
			String fUsoDelBit, String fCommento, String fLivello, String fUpdated,boolean flag_print) {
		super("",  "",  "",  fAddrSymPlc,  "none",  fTagNameSCADA,
				 fTipo,  fSviluppo,  fSigla,  fDescrizione,  fDescrizioneEstesa,
				 fLimiteMIN,  fLimiteMAX,  fUM,  fUsoDelBit, fCommento, fLivello, fUpdated, flag_print);
		this.fIndice = fIndice;
		this.fDescrizioneEstesa = fDescrizioneEstesa;
		
//		ModelRockwell.logRock.info("this.fAddrSymPlc: "+this.fAddrSymPlc);
//		ModelRockwell.logRock.info("this.fAddrAbsPlc: "+this.fAddrAbsPlc);
//		ModelRockwell.logRock.info("this.fTagNameSCADA: "+this.fTagNameSCADA);
//		ModelRockwell.logRock.info("this.fTipo: "+this.fTipo);
//		ModelRockwell.logRock.info("this.fSviluppo: "+this.fSviluppo);
//		ModelRockwell.logRock.info("this.fSigla: "+this.fSigla);
//		ModelRockwell.logRock.info("this.fDescrizione: "+this.fDescrizione);
//		ModelRockwell.logRock.info("this.fDescrizioneEstesa: "+this.fDescrizioneEstesa);
//		ModelRockwell.logRock.info("this.fLimiteMIN: "+this.fLimiteMIN);
//		ModelRockwell.logRock.info("this.fLimiteMAX: "+this.fLimiteMAX);
//		ModelRockwell.logRock.info("this.fUM: "+this.fUM);
//		ModelRockwell.logRock.info("this.fUsoDelBit: "+this.fUsoDelBit);
//		ModelRockwell.logRock.info("this.fCommento: "+this.fCommento);
//		ModelRockwell.logRock.info("this.fLivello: "+this.fLivello);
//		ModelRockwell.logRock.info("this.fUpdated: "+this.fUpdated);
//		ModelRockwell.logRock.info("this.flg_print: "+this.flg_print);
//		ModelRockwell.logRock.info("this.fIndice: "+this.fIndice);
		

	}
	
	
	
	/**
	 * Getter and Setter
	 */
	
	public String getfIndice() {
		return fIndice;
	}
	
	public void setfIndice(String fIndice) {
		this.fIndice = fIndice;
	}
	
}
