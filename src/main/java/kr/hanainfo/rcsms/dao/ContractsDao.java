package kr.hanainfo.rcsms.dao;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service(value = "contractsDao")
public class ContractsDao {
    @Resource(name = "contractsMapper")
    private ContractsMapper contractsMapper;


    public List<ContractsVo> getSelect() {
        return this.contractsMapper.select();
    }
    
    public ContractsVo getSelectOne(String cCode) {
    	return this.contractsMapper.selectOne(cCode);
    }
/*   
    public List<ContractsVo> selectSpecificOwnerId(String owner_id) {
    	return this.photosMapper.selectSpecificOwnerId(owner_id);
    }
    
    public ContractsVo selectRandomPhoto() {
    	return this.photosMapper.selectRandomPhoto();
    }
*/
    public void insert(ContractsVo contractsVo) {
         this.contractsMapper.insert(contractsVo);
    }
/*
    public void delete(String id) {
         this.photosMapper.delete(id);
    }
    
    public void deleteSpecificPhoto(int idx) {
    	this.photosMapper.deleteSpecificPhoto(idx);
    }
    
    public ContractsVo selectSpecificIdx(int idx) {
    	return this.photosMapper.selectSpecificIdx(idx);
    }
*/
}
