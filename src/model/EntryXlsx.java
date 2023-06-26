package model;

public abstract class EntryXlsx {
	
	protected String fAddrSymPlc;
	protected String fAddrAbsPlc;
	protected String fTagNameSCADA;
	protected String fTipo;
	protected int fSviluppo;
	protected String fSigla;
	protected String fDescrizione;
	protected String fDescrizioneEstesa;
	protected String fLimiteMIN;
	protected String fLimiteMAX;
	protected String fUM;
	protected String fUsoDelBit;
	protected String fCommento;
	protected String fLivello;
	protected String fUpdated;
	protected boolean flg_print;
	
	public EntryXlsx(String fDbNumber, String fWordNumber, String fBitNumber, String fAddrSymPlc, String fAddrAbsPlc, String fTagNameSCADA,
			String fTipo, int fSviluppo, String fSigla, String fDescrizione, String fDescrizioneEstesa,
			String fLimiteMIN, String fLimiteMAX, String fUM, String fUsoDelBit,String fCommento,String fLivello,String fUpdated,boolean flag_print) {
		this.fAddrSymPlc = fAddrSymPlc;
		this.fAddrAbsPlc = fAddrAbsPlc;
		this.fTagNameSCADA = fTagNameSCADA;
		this.fTipo = fTipo;
		this.fSviluppo = fSviluppo;
		this.fSigla = fSigla;
		this.fDescrizione = fDescrizione;
		this.fLimiteMIN = fLimiteMIN;
		this.fLimiteMAX = fLimiteMAX;
		this.fUM = fUM;
		this.fUsoDelBit = fUsoDelBit;
		this.fCommento=fCommento;
		this.fLivello=fLivello;
		this.fUpdated=fUpdated;
		this.flg_print=flag_print;
		

	}
	
	//-----
	/**
	 * @return the fAddrAbsPlc
	 */
	public String getfAddrAbsPlc() {
		return fAddrAbsPlc;
	}



	/**
	 * @return the fAddrSymPlc
	 */
	public String getfAddrPlc() {
		return fAddrSymPlc;
	}

	/**
	 * @return the fTagNameSCADA
	 */
	public String getfTagNameSCADA() {
		return fTagNameSCADA;
	}

	/**
	 * @return the fTipo
	 */
	public String getfTipo() {
		return fTipo;
	}

	/**
	 * @return the fSviluppo
	 */
	public int getfSviluppo() {
		return fSviluppo;
	}

	/**
	 * @return the fSigla
	 */
	public String getfSigla() {
		return fSigla;
	}

	/**
	 * @return the fDescrizione
	 */
	public String getfDescrizione() {
		return fDescrizione;
	}

	/**
	 * @return the fDescrizioneEstesa
	 */
	public String getfDescrizioneEstesa() {
		return fDescrizioneEstesa;
	}

	/**
	 * @return the fLimiteMIN
	 */
	public String getfLimiteMIN() {
		return fLimiteMIN;
	}

	/**
	 * @return the fLimiteMAX
	 */
	public String getfLimiteMAX() {
		return fLimiteMAX;
	}

	/**
	 * @return the fUM
	 */
	public String getfUM() {
		return fUM;
	}

	/**
	 * @return the fUsoDelBit
	 */
	public String getfUsoDelBit() {
		return fUsoDelBit;
	}

	/**
	 * @return the fCommento
	 */
	public String getfCommento() {
		return fCommento;
	}

	/**
	 * @return the fLivello
	 */
	public String getfLivello() {
		return fLivello;
	}

	/**
	 * @return the fUpdated
	 */
	public String getfUpdated() {
		return fUpdated;
	}

	/**
	 * @return the flg_print
	 */
	public boolean isFlg_print() {
		return flg_print;
	}

	/**
	 * @param fAddrAbsPlc the fAddrAbsPlc to set
	 */
	public void setfAddrAbsPlc(String fAddrAbsPlc) {
		this.fAddrAbsPlc = fAddrAbsPlc;
	}
	
	/**
	 * @param fAddrSymPlc the fAddrSymPlc to set
	 */
	public void setfAddrSymPlc(String fAddrSymPlc) {
		this.fAddrSymPlc = fAddrSymPlc;
	}

	/**
	 * @param fTagNameSCADA the fTagNameSCADA to set
	 */
	public void setfTagNameSCADA(String fTagNameSCADA) {
		this.fTagNameSCADA = fTagNameSCADA;
	}

	/**
	 * @param fTipo the fTipo to set
	 */
	public void setfTipo(String fTipo) {
		this.fTipo = fTipo;
	}

	/**
	 * @param fSviluppo the fSviluppo to set
	 */
	public void setfSviluppo(int fSviluppo) {
		this.fSviluppo = fSviluppo;
	}

	/**
	 * @param fSigla the fSigla to set
	 */
	public void setfSigla(String fSigla) {
		this.fSigla = fSigla;
	}

	/**
	 * @param fDescrizione the fDescrizione to set
	 */
	public void setfDescrizione(String fDescrizione) {
		this.fDescrizione = fDescrizione;
	}

	/**
	 * @param fDescrizioneEstesa the fDescrizioneEstesa to set
	 */
	public void setfDescrizioneEstesa(String fDescrizioneEstesa) {
		this.fDescrizioneEstesa = fDescrizioneEstesa;
	}

	/**
	 * @param fLimiteMIN the fLimiteMIN to set
	 */
	public void setfLimiteMIN(String fLimiteMIN) {
		this.fLimiteMIN = fLimiteMIN;
	}

	/**
	 * @param fLimiteMAX the fLimiteMAX to set
	 */
	public void setfLimiteMAX(String fLimiteMAX) {
		this.fLimiteMAX = fLimiteMAX;
	}

	/**
	 * @param fUM the fUM to set
	 */
	public void setfUM(String fUM) {
		this.fUM = fUM;
	}

	/**
	 * @param fUsoDelBit the fUsoDelBit to set
	 */
	public void setfUsoDelBit(String fUsoDelBit) {
		this.fUsoDelBit = fUsoDelBit;
	}

	/**
	 * @param fCommento the fCommento to set
	 */
	public void setfCommento(String fCommento) {
		this.fCommento = fCommento;
	}

	/**
	 * @param fLivello the fLivello to set
	 */
	public void setfLivello(String fLivello) {
		this.fLivello = fLivello;
	}

	/**
	 * @param fUpdated the fUpdated to set
	 */
	public void setfUpdated(String fUpdated) {
		this.fUpdated = fUpdated;
	}

	/**
	 * @param flg_print the flg_print to set
	 */
	public void setFlg_print(boolean flg_print) {
		this.flg_print = flg_print;
	}

}
