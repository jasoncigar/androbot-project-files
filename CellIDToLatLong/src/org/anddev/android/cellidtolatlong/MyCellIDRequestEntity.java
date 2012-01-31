package org.anddev.android.cellidtolatlong;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

class MyCellIDRequestEntity implements RequestEntity {

	protected int myCellID;
	protected int myLAC;

	public MyCellIDRequestEntity(int aCellID, int aLAC) {
		this.myCellID = aCellID;
		this.myLAC = aLAC;
	}

	public boolean isRepeatable() {
		return true;
	}

	/**
	 * Pretend to be a French Sony_Ericsson-K750 that wants to receive its
	 * lat/long-values =) The data written is highly proprietary !!!
	 */
	public void writeRequest(OutputStream outputStream) throws IOException {
		DataOutputStream os = new DataOutputStream(outputStream);
		os.writeShort(21);
		os.writeLong(0);
		os.writeUTF("fr");
		os.writeUTF("Sony_Ericsson-K750");
		os.writeUTF("1.3.1");
		os.writeUTF("Web");
		os.writeByte(27);

		os.writeInt(0);
		os.writeInt(0);
		os.writeInt(3);
		os.writeUTF("");
		os.writeInt(myCellID); // CELL-ID
		os.writeInt(myLAC); // LAC
		os.writeInt(0);
		os.writeInt(0);
		os.writeInt(0);
		os.writeInt(0);
		os.flush();
	}

	public long getContentLength() {
		return -1;
	}

	public String getContentType() {
		return "application/binary";
	}
}
