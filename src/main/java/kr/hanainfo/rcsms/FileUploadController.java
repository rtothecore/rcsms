package kr.hanainfo.rcsms;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.hanainfo.rcsms.dao.ContractsDao;
import kr.hanainfo.rcsms.dao.ContractsVo;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


@Controller(value = "fileUploadController")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    @Resource(name = "contractsDao")
    private ContractsDao contractsDao;
    
    /**
	 * Upload single file using Spring Controller
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody
	String uploadFileHandler(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
		String[] extension = file.getOriginalFilename().split("\\.");
		String tempFilename = timeStamp + "." + extension[1];
		
		File serverFile = null;
		int accessRowIdx = 0;
		char accessColIdx = 'A';
		
		if (validate(file)) {
			try {
				byte[] bytes = file.getBytes();			
				// 1. Creating the directory to store file
				ServletContext sc = request.getSession().getServletContext();
				File serverFolder = new File(sc.getRealPath("/resources/xlsxs"));		// original
				
				if (!serverFolder.exists()) {
					serverFolder.mkdirs();
				}					
				
				// 2. Create the file on server
				serverFile = new File(serverFolder + File.separator + tempFilename);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				
				// 3. Reading EXCEL file & check validate
				try {
					FileInputStream excelFile = new FileInputStream(new File(serverFile.toString()));
					Workbook workbook = new XSSFWorkbook(excelFile);
					Sheet datatypeSheet = workbook.getSheetAt(0);
					Iterator<Row> iterator = datatypeSheet.iterator();
	
					if(false == iterator.hasNext()) {
						throw new IOException("There is no data at first row");
					}
					
					while (iterator.hasNext()) {
						Row currentRow = iterator.next();
							
						accessColIdx = 'A';
						System.out.print(currentRow.getCell(0).getStringCellValue() + "|");
							
						accessColIdx = 'B';
						if(checkStringLength(20, currentRow.getCell(1).getStringCellValue().length())) {
							System.out.print(currentRow.getCell(1).getStringCellValue() + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						accessColIdx = 'E';
						if(checkStringLength(10, currentRow.getCell(4).getStringCellValue().length())) {
							System.out.print(currentRow.getCell(4).getStringCellValue() + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						accessColIdx = 'F';
						if(checkStringLength(25, currentRow.getCell(5).getStringCellValue().length())) {
							System.out.print(currentRow.getCell(5).getStringCellValue() + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						accessColIdx = 'G';
						if(checkStringLength(5, String.valueOf((int)currentRow.getCell(6).getNumericCellValue()).length())) {
							System.out.print(String.valueOf((int)currentRow.getCell(6).getNumericCellValue()) + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						accessColIdx = 'H';
						System.out.print(currentRow.getCell(7).getStringCellValue() + "|");
						
						accessColIdx = 'L';
						if(checkStringLength(10, currentRow.getCell(11).getStringCellValue().length())) {
							System.out.print(currentRow.getCell(11).getStringCellValue() + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						accessColIdx = 'M';
						if(checkStringLength(13, currentRow.getCell(12).getStringCellValue().length())) {
							System.out.print(currentRow.getCell(12).getStringCellValue() + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						accessColIdx = 'N';
						if(checkStringLength(30, currentRow.getCell(13).getStringCellValue().length())) {
							System.out.print(currentRow.getCell(13).getStringCellValue() + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						accessColIdx = 'O';
						if(checkStringLength(10, currentRow.getCell(14).getStringCellValue().length())) {
							System.out.print(currentRow.getCell(14).getStringCellValue() + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						accessColIdx = 'P';
						if (CellType.STRING == currentRow.getCell(15).getCellTypeEnum()) {
							System.out.print(currentRow.getCell(15).getStringCellValue() + "|");
						} else if(CellType.NUMERIC == currentRow.getCell(15).getCellTypeEnum()){
							System.out.print(String.valueOf(currentRow.getCell(15).getNumericCellValue()) + "|");
						} else {
							System.out.print(String.valueOf(currentRow.getCell(15).getDateCellValue()) + "|");
						}
						
						accessColIdx = 'Q';
						if (CellType.STRING == currentRow.getCell(16).getCellTypeEnum()) {
							if(checkStringLength(50, currentRow.getCell(16).getStringCellValue().length())) {
								System.out.print(currentRow.getCell(16).getStringCellValue() + "|");
							} else {
								throw new Exception("Too long or invalid data");
							}
						} else {
							if(checkStringLength(50, String.valueOf(currentRow.getCell(16).getDateCellValue()).length())) {
								System.out.print(String.valueOf(currentRow.getCell(16).getDateCellValue()) + "|");
							} else {
								throw new Exception("Too long or invalid data");
							}
						}
						
						accessColIdx = 'R';
						if (CellType.STRING == currentRow.getCell(17).getCellTypeEnum()) {
							if(checkStringLength(50, currentRow.getCell(17).getStringCellValue().length())) {
								System.out.print(currentRow.getCell(17).getStringCellValue() + "|");
							} else {
								throw new Exception("Too long or invalid data");
							}
						} else {
							if(checkStringLength(50, String.valueOf(currentRow.getCell(17).getDateCellValue()).length())) {
								System.out.print(String.valueOf(currentRow.getCell(17).getDateCellValue()) + "|");
							} else {
								throw new Exception("Too long or invalid data");
							}
						}
						
						accessColIdx = 'S';
						if(checkStringLength(30, currentRow.getCell(18).getStringCellValue().length())) {
							System.out.print(currentRow.getCell(18).getStringCellValue() + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						accessColIdx = 'T';
						if(checkStringLength(30, currentRow.getCell(19).getStringCellValue().length())) {
							System.out.print(currentRow.getCell(19).getStringCellValue() + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						accessColIdx = 'V';
						if(checkStringLength(30, currentRow.getCell(21).getStringCellValue().length())) {
							System.out.print(currentRow.getCell(21).getStringCellValue() + "|");
						} else {
							throw new Exception("Too long or invalid data");
						}
						
						System.out.println();
						accessRowIdx++;
			        }
					workbook.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					serverFile.delete();
					return "You failed to upload " + tempFilename + " => " + e.getMessage();
				} catch (IOException e) {
					e.printStackTrace();
					serverFile.delete();
					return "You failed to upload " + tempFilename + " => " + e.getMessage();
				}
				
				accessRowIdx = 0;
				accessColIdx = 'X';
				
				// 4. insert DB
				try {
					FileInputStream excelFile = new FileInputStream(new File(serverFile.toString()));
					Workbook workbook = new XSSFWorkbook(excelFile);
					Sheet datatypeSheet = workbook.getSheetAt(0);
					Iterator<Row> iterator = datatypeSheet.iterator();
					
					while (iterator.hasNext()) {
						Row currentRow = iterator.next();
						
						ContractsVo contractsVo = new ContractsVo();	
						
						contractsVo.setTitle(currentRow.getCell(0).getStringCellValue());
						contractsVo.setBuyDate(currentRow.getCell(1).getStringCellValue());
						contractsVo.setcCount(currentRow.getCell(4).getStringCellValue());
						contractsVo.setcCode(currentRow.getCell(5).getStringCellValue());
						contractsVo.setDateCode(String.valueOf((int)currentRow.getCell(6).getNumericCellValue()));
						contractsVo.setcDesc(currentRow.getCell(7).getStringCellValue());
						contractsVo.setcName(currentRow.getCell(11).getStringCellValue());
						contractsVo.setPhone(currentRow.getCell(12).getStringCellValue());
						contractsVo.setEmail(currentRow.getCell(13).getStringCellValue());
						contractsVo.setFee(currentRow.getCell(14).getStringCellValue());
						
						if (CellType.STRING == currentRow.getCell(15).getCellTypeEnum()) {
							contractsVo.setcDesc2(currentRow.getCell(15).getStringCellValue());
						} else if(CellType.NUMERIC == currentRow.getCell(15).getCellTypeEnum()){
							contractsVo.setcDesc2(String.valueOf(currentRow.getCell(15).getNumericCellValue()));
						} else {
							contractsVo.setcDesc2(String.valueOf(currentRow.getCell(15).getDateCellValue()));
						}
						
						if (CellType.STRING == currentRow.getCell(16).getCellTypeEnum()) {
							contractsVo.setStartDate(currentRow.getCell(16).getStringCellValue());
						} else {
							contractsVo.setStartDate(String.valueOf(currentRow.getCell(16).getDateCellValue()));
						}
						
						if (CellType.STRING == currentRow.getCell(17).getCellTypeEnum()) {
							contractsVo.setEndDate(currentRow.getCell(17).getStringCellValue());
						} else {
							contractsVo.setEndDate(String.valueOf(currentRow.getCell(17).getDateCellValue()));
						}
						
						contractsVo.setCompany(currentRow.getCell(18).getStringCellValue());
						contractsVo.setCarName(currentRow.getCell(19).getStringCellValue());
						contractsVo.setFeeType(currentRow.getCell(21).getStringCellValue());
						contractsVo.setOp1("");
						contractsVo.setOp2("");
						contractsVo.setOp3("");
						contractsVo.setOp4("");
						contractsVo.setOp5("");
						
						this.contractsDao.insert(contractsVo);
						accessRowIdx++;
					}
					workbook.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					serverFile.delete();
					return "You failed to insert DB " + tempFilename + " => " + e.getMessage();
				} catch (IOException e) {
					e.printStackTrace();
					serverFile.delete();
					return "You failed to insert DB " + tempFilename + " => " + e.getMessage();
				}

				logger.info("Server File Location=" + serverFile.getCanonicalPath());

				return "You successfully uploaded file=" + tempFilename;
			} catch (Exception e) {
				serverFile.delete();
				return "You failed to upload " + tempFilename + " => " + e.getMessage() + ", Error at (" + (accessRowIdx+1) + ", " + accessColIdx + ")";
			}
		} else {
			serverFile.delete();
			return "You failed to upload " + tempFilename + " because the file was empty or wrong file type.";
		}
	}
	
	/**
	 * Validate upload file type
	 */
	public boolean validate(Object uploadedFile) {
		MultipartFile file = (MultipartFile) uploadedFile;
		
		if (file.isEmpty() || file.getSize() == 0) {
			logger.info("file is empty || getsize() == 0");
			return false;
		}
		
		if (!(file.getContentType().toLowerCase().equals("application/vnd.ms-excel")
					|| file.getContentType().toLowerCase().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
			logger.info("file type: " + file.getContentType().toLowerCase());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Validate string length
	 */
	public boolean checkStringLength(int dbLength, int xlsxLength) {
		if(dbLength < xlsxLength) {
			return false;
		}
		return true;
	}
	
	/**
	 * Get list of contracts 
	 */
	@RequestMapping(value = "/getContracts", method = RequestMethod.POST)
	public @ResponseBody List<ContractsVo> getContracts(HttpSession session) {
		logger.info("Get list of contracts");
		List<ContractsVo> list = this.contractsDao.getSelect();

		return list;
	}

	/**
	 * Send SMS
	 */
	@RequestMapping(value = "/sendSMS", method = RequestMethod.POST)
	public @ResponseBody
	boolean sendSMS(@RequestBody ContractsVo cv, HttpServletRequest request) {
		
		logger.info("cIdx:" + cv.getIdx() + ", contractName:" + cv.getcName() + ", contractPhone:" + cv.getPhone() + ", contractDesc:" + cv.getcDesc());
		
		// 1. send SMS
		
		// 2. update DB
		
		return true;
	}
	
	
}
