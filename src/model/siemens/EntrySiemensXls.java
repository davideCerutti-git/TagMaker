package model.siemens;

import model.EntryXlsx;

public class EntrySiemensXls extends EntryXlsx {

	private String fDbNumber;
	private String fWordNumber;
	private String fBitNumber;
	private String fAddrAbsPlc;

	public EntrySiemensXls(
			String fDbNumber,
			String fWordNumber,
			String fBitNumber, 
			String fAddrSymPlc,
			String fAddrAbsPlc, 
			String fTagNameSCADA,
			String fTipo,
			int fSviluppo, 
			String fSigla, 
			String fDescrizione,
			String fDescrizioneEstesa,
			String fLimiteMIN, 
			String fLimiteMAX, 
			String fUM,
			String fUsoDelBit,
			String fCommento, 
			String fLivello, 
			String fUpdated,
			boolean flag_print) {
		super(fDbNumber, fWordNumber, fBitNumber, fAddrSymPlc, fAddrAbsPlc, fTagNameSCADA, fTipo, fSviluppo, fSigla,
				fDescrizione, fDescrizioneEstesa, fLimiteMIN, fLimiteMAX, fUM, fUsoDelBit, fCommento, fLivello,
				fUpdated, flag_print);
		this.fDbNumber = fDbNumber;
		this.fWordNumber = fWordNumber;
		this.fBitNumber = fBitNumber;
		this.fAddrAbsPlc = fAddrAbsPlc;
		this.fDescrizioneEstesa = fDescrizioneEstesa.replaceAll("\"", "").replaceAll(";", "").trim();
		
//		System.out.println(fDescrizioneEstesa);
	}

	/**
	 * Getter and Setter
	 */

	/**
	 * @return the fAddrAbsPlc
	 */
	public String getfAddrAbsPlc() {
		return fAddrAbsPlc;
	}

	/**
	 * @return the fDbNumber
	 */
	public String getfDbNumber() {
		return fDbNumber;
	}

	/**
	 * @return the fWordNumber
	 */
	public String getfWordNumber() {
		return fWordNumber;
	}

	/**
	 * @return the fBitNumber
	 */
	public String getfBitNumber() {
		return fBitNumber;
	}

	/**
	 * @param fDbNumber the fDbNumber to set
	 */
	public void setfDbNumber(String fDbNumber) {
		this.fDbNumber = fDbNumber;
	}

	/**
	 * @param fWordNumber the fWordNumber to set
	 */
	public void setfWordNumber(String fWordNumber) {
		this.fWordNumber = fWordNumber;
	}

	/**
	 * @param fBitNumber the fBitNumber to set
	 */
	public void setfBitNumber(String fBitNumber) {
		this.fBitNumber = fBitNumber;
	}

	/**
	 * @param fAddrAbsPlc the fAddrAbsPlc to set
	 */
	public void setfAddrAbsPlc(String fAddrAbsPlc) {
		this.fAddrAbsPlc = fAddrAbsPlc;
	}

}
