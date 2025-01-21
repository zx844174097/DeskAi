package cn.net.mugui.net.pc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import com.mugui.Mugui;
import com.mugui.tool.BufferedRandomAccessFile;

import cn.hutool.core.io.resource.ResourceUtil;

public class DimgFile implements Mugui {
	public BufferedImage bufferedImage = null;
	public File file = null;

	public void saveAllData() {
		if (bufferedImage != null && file != null)
			try {
				ImageIO.write(bufferedImage, "bmp", file);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public static DimgFile getImgFile(String filepath) {
		File file = new File(filepath);
		if (!file.isFile()) {
			file = new File(filepath);
		}
		return getImgFile(new File(filepath));
	}

	public static InputStream getInputString(File file) throws FileNotFoundException {
		InputStream inputStream = null;
		if (file.isFile()) {
			inputStream = new FileInputStream(file);
			return inputStream;
		}
		inputStream = ResourceUtil.getStream(file.getName());
		return inputStream;
	}

	public static DimgFile getImgFile(File file2) {
		try {
			InputStream inputStream = getInputString(file2);
			if (inputStream == null)
				return null;
			DataInputStream dataInputStream = null;
			DimgFile dimgFile = new DimgFile();
			dataInputStream = new DataInputStream(inputStream);
			int data = 0;
			if ((data = dataInputStream.read()) == '{') {

				byte[] b = new byte[1024 * 8];
				int bi = 0;
				int i = 0;
				while ((i = dataInputStream.read()) != '}') {
					b[bi++] = (byte) i;
				}
				bi += 2;
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			if (data != '{')
				bos.write(data);
			byte bbb[] = new byte[1024];
			int sss = 0;
			while ((sss = dataInputStream.read(bbb)) != -1) {
				bos.write(bbb, 0, sss);
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(bos.toByteArray());
			ImageInputStream imageInputStream = ImageIO.createImageInputStream(bais);
			dimgFile.bufferedImage = ImageIO.read(imageInputStream);
			if (dimgFile.bufferedImage == null)
				throw new IOException("bufferedImage create bad");
			bais.close();
			dataInputStream.close();
			inputStream.close();
			dimgFile.file = file2;
			return dimgFile;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
}
