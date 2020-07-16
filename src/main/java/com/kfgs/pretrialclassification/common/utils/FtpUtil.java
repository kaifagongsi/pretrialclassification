package com.kfgs.pretrialclassification.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtil {
	
	/**
	 * 获得ftp连接
	 * @param ftpHost
	 * @param ftpUserName
	 * @param ftpPassWord
	 * @param ftpPort
	 * @return
	 */
	public FTPClient getFtpClient(String ftpHost, String ftpUserName, String ftpPassWord, int ftpPort) {
		FTPClient ftpClient = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(ftpHost, ftpPort);
			ftpClient.login(ftpUserName, ftpPassWord);
			if(!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				System.out.println("未连接到FTP，用户名或密码错误！！！");
			} else {
				System.out.println("FTP连接成功！！！");
			}
		} catch (SocketException e) {
			System.out.println("FTP的IP地址可能错误！");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("FTP的port可能错误！");
			e.printStackTrace();
		}
		return ftpClient;
	}
	
	
	
	/**
	 * ftp上传文件
	 * @param ftpHost
	 * @param ftpUserName
	 * @param ftpPassWord
	 * @param ftpPort
	 * @param ftpPath
	 * @param uploadList
	 * @return
	 */
	public boolean uploadFile(String ftpHost, String ftpUserName, String ftpPassWord,
			int ftpPort, String ftpPath, File[] uploadList) {
		
		System.out.println("开始上传文件");
		boolean success = false;
		FTPClient ftpClient = null;
		FileInputStream input = null;
		
		try {
			int reply;
			ftpClient = getFtpClient(ftpHost, ftpUserName, ftpPassWord, ftpPort);
			reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				System.out.println("ftp获取reply失败，关闭ftp连接！！！");
				return false;
			}
			ftpClient.setControlEncoding("UTF-8");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalActiveMode();
			ftpClient.changeWorkingDirectory(ftpPath);
			
			for (int i = 0; i < uploadList.length; i++) {
				if(!uploadList[i].getName().endsWith(".doc")) continue;
				input = new FileInputStream(uploadList[i]);
				ftpClient.storeFile(uploadList[i].getName(), input);
			}
			
			input.close();
			ftpClient.logout();
			success = true;
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	/*
     * 从FTP服务器下载文件
     *
     * @param ftpHost FTP IP地址
     * @param ftpUserName FTP 用户名
     * @param ftpPassword FTP用户名密码
     * @param ftpPort FTP端口
     * @param ftpPath FTP服务器中文件所在路径 格式： ftptest/aa
     * @param localPath 下载到本地的位置 格式：H:/download
     * @param fileName 文件名称
     */
    public void downloadFtpFile(String ftpHost, String ftpUserName,
                                       String ftpPassword, int ftpPort, String ftpPath, String localPath,
                                       List<String> fileNameList) {

        FTPClient ftpClient = null;

        try {
            ftpClient = getFtpClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(ftpPath);
            
            OutputStream os = null;
            for(int i=0; i<fileNameList.size(); i++){
            	if(fileNameList.get(i).endsWith(".xml")){
            		
            	}else {					
            		String fileName = localPath + File.separatorChar + fileNameList.get(i);
            		File localFile = new File(fileName);
            		os = new FileOutputStream(localFile);
            		ftpClient.retrieveFile(fileNameList.get(i), os);
            		System.out.println(fileNameList.get(i)+":下载成功");
				}	
            }
            System.out.println("总共"+fileNameList.size()+"件专利");
            os.flush();
            os.close();
            ftpClient.logout();

        } catch (FileNotFoundException e) {
            System.out.println("没有找到" + ftpPath + "文件");
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("连接FTP失败.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件读取错误。");
            e.printStackTrace();
        }

    }
	
	
	
	
	public static void main(String[] args) {
		FtpUtil fu = new FtpUtil();
		
		String ftpHost = "192.168.8.130";
		String ftpUserName = "baohuUser";
		String ftpPassWord = "123456";
		int ftpPort = 21;
		
		String ftpPath = "";
		String localPath = "C:\\Users\\User\\Desktop\\FTPdownload";
		String localDocxPath = "C:\\Users\\Administrator\\Desktop\\docx";

		//FTP连接·
		fu.getFtpClient(ftpHost, ftpUserName, ftpPassWord, ftpPort);   //success

		//文件下载
		List<String> fileNameList = new ArrayList<String>();
		fileNameList.add("FS-2019-IM-1-0001.doc");
		fileNameList.add("NJ-2019-IT-1-0174.pdf");
		fu.downloadFtpFile(ftpHost, ftpUserName,ftpPassWord, ftpPort, ftpPath, localPath, fileNameList);

		
		//文件上传
		/*File localDocxDir = new File(localDocxPath);
		File[] uploadList = localDocxDir.listFiles();
		Boolean flag = fu.uploadFile(ftpHost, ftpUserName, ftpPassWord, ftpPort, ftpPath, uploadList);
		if(flag) System.out.println("文件上传成功");*/
		
		
	}

}
