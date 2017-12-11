package comServCli;

import java.io.File;
import java.io.Serializable;

/**
 * Created by ctx on 04/12/17.
 */
public class P2PFile implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nameFile;
	private long sizeFile;
	
	public P2PFile(File file) {
		this.nameFile = file.getName();
		this.sizeFile = file.length();
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	public long getSizeFile() {
		return sizeFile;
	}

	public void setSizeFile(long sizeFile) {
		this.sizeFile = sizeFile;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nameFile == null) ? 0 : nameFile.hashCode());
		result = prime * result + (int) (sizeFile ^ (sizeFile >>> 32));
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		P2PFile other = (P2PFile) obj;
		if (nameFile == null) {
			if (other.nameFile != null)
				return false;
		} else if (!nameFile.equals(other.nameFile))
			return false;
		if (sizeFile != other.sizeFile)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.nameFile + ":" + this.sizeFile;
	}
}
