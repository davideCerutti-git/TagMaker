package model.siemens;

public class Address {
	private int DB;
	private int Byte;
	private int Bit;

	
	public Address(int numDB, int numByte, int numBit) {
		super();
		this.DB = numDB;
		this.Byte = numByte;
		this.Bit = numBit;
	}
	
	public Address() {
		super();
		this.DB = 0;
		this.Byte = 0;
		this.Bit = 0;
	}

	public void addBit(){
//		ModelSiemens.logSiem.info("addBit");
		if(this.Bit==7){
			this.Bit=0;
			this.Byte++;
		}else
			this.Bit++;
	}
	
	public void addBit(int bit){
//		ModelSiemens.logSiem.info("addBit");
		for(int i=bit;i>0;i--){
			addBit();
		}
	}
	
	public void addByte(){
//		ModelSiemens.logSiem.info("addByte");
		this.Byte++;
	}
	
	public void addByte(int num_byte){
//		ModelSiemens.logSiem.info("addByte");
		for(int i=num_byte;i>0;i--){
			addByte();
		}
	}

	public int gByte() {
		return Byte;
	}

	public int gBit() {
		return Bit;
	}
	
	public int getDB() {
		return DB;
	}
	
	public String toWeintekBit(){
		StringBuffer str=new StringBuffer("");
		str.append(this.getDB());
		if(this.gByte()<=9){
			str.append("000").append(this.gByte());
		}else if(this.gByte()>=10 && this.gByte()<=99){
			str.append("00").append(this.gByte());
		}else if(this.gByte()>=100 && this.gByte()<=999){
			str.append("0").append(this.gByte());
		}else if(this.gByte()>=1000){
			str.append(this.gByte());
		}
		str.append(this.gBit());
		return str.toString();
	}
	
	public String toWeintekByte(){
		StringBuffer str=new StringBuffer("");
		str.append(this.getDB());
		if(this.gByte()<=9){
			str.append("000").append(this.gByte());
		}else if(this.gByte()>=10 && this.gByte()<=99){
			str.append("00").append(this.gByte());
		}else if(this.gByte()>=100 && this.gByte()<=999){
			str.append("0").append(this.gByte());
		}else if(this.gByte()>=1000){
			str.append(this.gByte());
		}
		return str.toString();
	}

	@Override
	public Address clone() throws CloneNotSupportedException {
		return new Address(this.getDB(),this.gByte(),this.gBit());
	}

	@Override
	public String toString() {
		return "[DB"+DB + "-B" + Byte + "." + Bit + "]";
	}

	public String toSiemensBit() {
//(DB86.DBX202.1)
		//System.out.println(""+"(DB"+numDB+".DBX"+numByte+"."+numBit+")");
		return ""+"(DB"+DB+".DBX"+Byte+"."+Bit+")";
	}
	
	
//	public int getByte() {
//		return this.numByte;
//	}
//
//	public int getBit() {
//		return this.numBit;
//	}

	public void add(Address add) {
//		ModelSiemens.logSiem.info("add");
		this.addByte(add.gByte());
		this.addBit(add.gBit());
	}

	public void setDB_fromAddress(Address gAddr) {
		this.setDB(gAddr.getDB());
	}
	
	public void setDB(int numDB) {
		this.DB = numDB;
	}

	public void setByte(int numByte) {
		this.Byte = numByte;
	}

	public void setBit(int numBit) {
		this.Bit = numBit;
	}

	public void resetAddress() {
		this.DB = 0;
		this.Byte = 0;
		this.Bit = 0;		
	}

	public void incrByte(int i) {
//		ModelSiemens.logSiem.info("incrByte");
		this.Byte=this.Byte+i;		
	}
	
	public void incrBit() {
		this.Bit++;		
	}

	public void incrementAddress(int i, int j) {
//		ModelSiemens.logSiem.info("incrementAddress");
		this.Byte = this.Byte+i;
		this.Bit = this.Bit+j;
		if(this.Bit>7) {
			this.Byte = this.Byte+1;
			this.Bit = 0;
		}
	}
	
	
}
