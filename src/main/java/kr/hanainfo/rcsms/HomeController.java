package kr.hanainfo.rcsms;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kr.hanainfo.rcsms.dao.ContractsDao;
import kr.hanainfo.rcsms.dao.ContractsVo;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Resource(name = "contractsDao")
    private ContractsDao contractsDao;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String dispTest2(Model model) {
        logger.info("display upload.jsp");
        
        return "upload";
    }
	
	// 렌트카 예약현황 보기
    // PathVariable 어노테이션을 이용하여 RESTful 방식 적용
    // rcsms/90B930-15000010277645
    @RequestMapping("/{cCode}")
    public String dispBbsView(@PathVariable String cCode, Model model) {
        logger.info("display view cCode = {}", cCode);
        
        ContractsVo object = this.contractsDao.getSelectOne(cCode);
        model.addAttribute("object", object);
        
        return "contract_view";
    }
	
}
