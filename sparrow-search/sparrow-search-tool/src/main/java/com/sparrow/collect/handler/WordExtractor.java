package com.sparrow.collect.handler;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.LittleEndian;

public class WordExtractor {
	public WordExtractor() {
	}

	public String extractText(InputStream in) throws IOException {
		ArrayList text = new ArrayList();
		POIFSFileSystem fsys = new POIFSFileSystem(in);

		DocumentEntry headerProps = (DocumentEntry) fsys.getRoot().getEntry(
				"WordDocument");
		DocumentInputStream din = fsys
				.createDocumentInputStream("WordDocument");
		byte[] header = new byte[headerProps.getSize()];

		din.read(header);
		din.close();
		// Prende le informazioni dall'header del documento
		int info = LittleEndian.getShort(header, 0xa);

		boolean useTable1 = (info & 0x200) != 0;

		// boolean useTable1 = true;
		// Prende informazioni dalla piece table
		int complexOffset = LittleEndian.getInt(header, 0x1a2);
		// int complexOffset = LittleEndian.getInt(header);
		String tableName = null;
		if (useTable1) {
			tableName = "1Table";
		} else {
			tableName = "0Table";
		}
		DocumentEntry table = (DocumentEntry) fsys.getRoot()
				.getEntry(tableName);
		byte[] tableStream = new byte[table.getSize()];

		din = fsys.createDocumentInputStream(tableName);

		din.read(tableStream);
		din.close();

		din = null;
		fsys = null;
		table = null;
		headerProps = null;

		int multiple = findText(tableStream, complexOffset, text);

		StringBuffer sb = new StringBuffer();
		int size = text.size();
		tableStream = null;
		for (int x = 0; x < size; x++) {
			WordTextPiece nextPiece = (WordTextPiece) text.get(x);
			int start = nextPiece.getStart();
			int length = nextPiece.getLength();

			boolean unicode = nextPiece.usesUnicode();
			String toStr = null;
			if (unicode) {
				toStr = new String(header, start, length * multiple, "UTF-16LE");
			} else {
				toStr = new String(header, start, length, "ISO-8859-1");
			}
			sb.append(toStr).append(" ");

		}
		return sb.toString();
	}

	private static int findText(byte[] tableStream, int complexOffset,
			ArrayList text) throws IOException {
		// actual text
		int pos = complexOffset;
		int multiple = 2;
		// skips through the prms before we reach the piece table. These contain
		// data
		// for actual fast saved files
		while (tableStream[pos] == 1) {
			pos++;
			int skip = LittleEndian.getShort(tableStream, pos);
			pos += 2 + skip;
		}
		if (tableStream[pos] != 2) {
			throw new IOException("corrupted Word file");
		} else {
			// parse out the text pieces
			int pieceTableSize = LittleEndian.getInt(tableStream, ++pos);
			pos += 4;
			int pieces = (pieceTableSize - 4) / 12;
			for (int x = 0; x < pieces; x++) {
				int filePos = LittleEndian.getInt(tableStream, pos
						+ ((pieces + 1) * 4) + (x * 8) + 2);
				boolean unicode = false;
				if ((filePos & 0x40000000) == 0) {
					unicode = true;
				} else {
					unicode = false;
					multiple = 1;
					filePos &= ~(0x40000000); // gives me FC in doc stream
					filePos /= 2;
				}
				int totLength = LittleEndian.getInt(tableStream, pos + (x + 1)
						* 4)
						- LittleEndian.getInt(tableStream, pos + (x * 4));

				WordTextPiece piece = new WordTextPiece(filePos, totLength,
						unicode);
				text.add(piece);

			}

		}
		return multiple;
	}

	public boolean writeWordFile(String path, String content) {
		boolean w = false;
		try {
			// byte b[] = content.getBytes("ISO-8859-1");
			byte b[] = content.getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			POIFSFileSystem fs = new POIFSFileSystem();
			DirectoryEntry directory = fs.getRoot();
			DocumentEntry de = directory.createDocument("WordDocument", bais);
			FileOutputStream ostream = new FileOutputStream(path);
			fs.writeFilesystem(ostream);
			bais.close();
			ostream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return w;
	}

}

class WordTextPiece {
	private int _fcStart;
	private boolean _usesUnicode;
	private int _length;

	public WordTextPiece(int start, int length, boolean unicode) {
		_usesUnicode = unicode;
		_length = length;
		_fcStart = start;
	}

	public boolean usesUnicode() {
		return _usesUnicode;
	}

	public int getStart() {
		return _fcStart;
	}

	public int getLength() {
		return _length;
	}
}
