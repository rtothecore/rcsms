package kr.hanainfo.rcsms.dao;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository(value = "contractsMapper")
public interface ContractsMapper {
	
    List<ContractsVo> select();
    ContractsVo selectOne(String cCode);
    List<ContractsVo> selectWithDate(String regDate);
    List<ContractsVo> selectWithDataNSendSMS(String regDate);
    void updateSendSMS(String idx);
    void updateShortURL(String idx, String shortURL);
    /*
    List<ContractsVo> selectSpecificOwnerId(String owner_id);
    ContractsVo selectRandomPhoto();
    */
    void insert(ContractsVo contractsVo);
    /*
    void delete(String id);
    void deleteSpecificPhoto(int idx);
    ContractsVo selectSpecificIdx(int idx);
    */
}
