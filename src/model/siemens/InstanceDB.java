package model.siemens;

public class InstanceDB extends DB{
	private String associatedFB;

	public InstanceDB(int _numDataBlock, String _titleDataBlock, float _versionDataBlock, String _associatedFB) {
		super(_numDataBlock, _titleDataBlock, "", _versionDataBlock);
		this.associatedFB=_associatedFB;
	}

}
