package kr.hanainfo.rcsms;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
//import org.json.simple.JSONObject;
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

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.UrlshortenerScopes;

import kr.hanainfo.rcsms.dao.ContractsDao;
import kr.hanainfo.rcsms.dao.ContractsVo;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


@Controller(value = "fileUploadController")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    @Resource(name = "contractsDao")
    private ContractsDao contractsDao;
    
    // Google 단축URL 사용을 위한 URL
    public static final String SHORTENER_URL = "https://www.googleapis.com/urlshortener/v1/url?key=";
    public static final String API_KEY = "AIzaSyBXkHUIDpkPUQahj1UttBQuShfATwqygrg"; // 새로운 키 등록 필요
     
    //#######################################################################################
    //      : 단축시킬 URL 주소를 String 문자열로 입력받고, Google API에 전송 (JSON 첨부)
    //      : 결과 JSON String 데이터를 수신하여, JSONObject 혹은 Map(현재주석처리)으로 변환
    //      : JSONObject 에서 단축URL을 String 타입으로 return
    //      : 인증키당 일 100,000 변환 가능
    //#######################################################################################  
    public static String getShortenUrl(String originalUrl) {
         
        //System.out.println("[DEBUG] INPUT_URL : " + originalUrl );
         
        // Exception에 대비해 결과 URL은 처음에 입력 URL로 셋팅
        String resultUrl = originalUrl;
         
        // Google Shorten URL API는 JSON으로 longUrl 파라미터를 사용하므로, JSON String 데이터 생성
        String originalUrlJsonStr = "{\"longUrl\":\"" + originalUrl + "\"}";
        //System.out.println("[DEBUG] INPUT_JSON : " + originalUrlJsonStr);
         
        // Google에 변환 요청을 보내기위해 java.net.URL, java.net.HttpURLConnection 사용
        URL                 url         = null;
        HttpURLConnection   connection  = null;
        OutputStreamWriter  osw         = null;
        BufferedReader      br          = null;
        StringBuffer        sb          = null; // Google의 단축URL서비스 결과 JSON String Data
        JSONObject          jsonObj     = null; // 결과 JSON String Data로 생성할 JSON Object
         
        // Google 단축 URL 요청을 위한 주소 - https://www.googleapis.com/urlshortener/v1/url
        // get방식으로 key(사용자키) 파라미터와, JSON 데이터로 longUrl(단축시킬 원본 URL이 담긴 JSON 데이터) 를 셋팅하여 전송
        try {
            url = new URL(SHORTENER_URL + API_KEY);
            //System.out.println("[DEBUG] DESTINATION_URL : " + url.toString() );
             
        }catch(Exception e){
            System.out.println("[ERROR] URL set Failed");
            e.printStackTrace();
            return resultUrl;
        }
         
        // 지정된 URL로 연결 설정
        try{
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "toolbar");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
        }catch(Exception e){
            System.out.println("[ERROR] Connection open Failed");
            e.printStackTrace();
            return resultUrl;
        }
         
        // 결과 JSON String 데이터를 StringBuffer에 저장
        // 필요에 따라 JSON Obejct 혹은 Map으로 셋팅 (현재 Map은 주석처리)
        try{
            // Google 단축URL 서비스 요청
            osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(originalUrlJsonStr);
            osw.flush();
 
            // BufferedReader에 Google에서 받은 데이터를 넣어줌
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
             
            // BufferedReader 내 데이터 StringBuffer sb 에 저장
            sb = new StringBuffer();
            String buf = "";
            while ((buf = br.readLine()) != null) {
                sb.append(buf);
            }
            //System.out.println("[DEBUG] RESULT_JSON_DATA : " + sb.toString());
             
            // Google에서 받은 JSON String을 JSONObject로 변환
            jsonObj = new JSONObject(sb.toString()); 
            resultUrl = jsonObj.getString("id");
             
        }catch (Exception e) {
            System.out.println("[ERROR] Result JSON Data(From Google) set JSONObject Failed");
            e.printStackTrace();
            return resultUrl;
        }finally{
            if (osw != null)    try{ osw.close();   } catch(Exception e) { e.printStackTrace(); }
            if (br  != null)    try{ br.close();    } catch(Exception e) { e.printStackTrace(); }
        }
         
        //System.out.println("[DEBUG] RESULT_URL : " + resultUrl);
        return resultUrl;
    }
    
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
						
						// goo.gl ShortenURL lib - https://developers.google.com/api-client-library/java/apis/urlshortener/v1
						String longURL = "http://hanainfo.kr:8080/rcsms/" + contractsVo.getcCode();
						String shortURL = getShortenUrl(longURL);
						logger.info("Short URL=" + shortURL);
						contractsVo.setOp1(shortURL);
						contractsVo.setOp2("");
						contractsVo.setOp3("");
						contractsVo.setOp4("");
						contractsVo.setOp5("");
						
						// 4-1. check duplicated cCode
						if( null == this.contractsDao.getSelectOne(contractsVo.getcCode()) ) {
							// 4-2. insert unduplicated data
							this.contractsDao.insert(contractsVo);
						}
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
				serverFile.delete();	// Delete successful process file!
				
				return "You successfully processing file=" + tempFilename;
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
	 * Get list of contracts with date 
	 */
	@RequestMapping(value = "/getContractsWithDate", method = RequestMethod.POST)
	public @ResponseBody List<ContractsVo> getContractsWithDate(@RequestBody ContractsVo cv, HttpSession session) {
		logger.info("Get list of contracts with date");
		List<ContractsVo> list = this.contractsDao.getSelectWithDate(cv.getRegDate());

		return list;
	}

	/**
	 * Send SMS
	 */
	@RequestMapping(value = "/sendSMS", method = RequestMethod.POST)
	public @ResponseBody
	boolean sendSMS(@RequestBody ContractsVo cv, HttpServletRequest request) {
		// 1. send SMS
		logger.info("sendSMS!-cIdx:" + cv.getIdx() + ", contractName:" + cv.getcName() 
					+ ", contractPhone:" + cv.getPhone() + ", contractDesc:" + cv.getcDesc() + ", link:" + cv.getOp1() + ", rowspan:" + cv.getOp2());
		
		// 2. update DB
		int startIdx = cv.getIdx();
		int rowspanVal = Integer.parseInt(cv.getOp2());
		int idx = 0;
		while(idx < rowspanVal) {
			this.contractsDao.updateSendSMS(String.valueOf(startIdx++));
			idx++;
		}
		
		return true;
	}
	
	/**
	 * Send all SMS with date
	 */
	@RequestMapping(value = "/sendSMSAllWithDate", method = RequestMethod.POST)
	public @ResponseBody
	boolean sendSMSAllWithDate(@RequestBody ContractsVo cv, HttpServletRequest request) {
		
		logger.info("Get list of contracts with regDate:" + cv.getRegDate());
		
		// 1. select contracts with date and sendSMS ='N'
		List<ContractsVo> list = this.contractsDao.getSelectWithDataNSendSMS(cv.getRegDate());
		
		String beforeName = "";
		// 2. send SMS for selected contracts
		for(int i=0; i<list.size(); i++) {
			if(!beforeName.equals(list.get(i).getcName())) {	// check continuous record(checking same cName )
				logger.info("sendSMS ALL!-" + list.get(i).getcName() + "|" + list.get(i).getPhone() + "|" + list.get(i).getcDesc() + "|" + list.get(i).getOp1());
				beforeName = list.get(i).getcName();
			}
			
			// 3. update DB
			this.contractsDao.updateSendSMS(String.valueOf(list.get(i).getIdx()));
		}
		
		return true;
	}
	
}
