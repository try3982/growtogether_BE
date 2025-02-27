package com.campfiredev.growtogether.exception.test;

import static com.campfiredev.growtogether.exception.response.ErrorCode.*;

import com.campfiredev.growtogether.exception.custom.CustomException;
import org.springframework.stereotype.Service;

/**
 * 테스트용
 */
@Service
public class TestService {

  public void throwCustomException(String a){
    if(a.equals("throw")){
      throw new CustomException(ALREADY_JOINED_STUDY);
    }
  }

  public void throwException(String a) throws IllegalAccessException {
    if(a.equals("throw")){
      throw new IllegalAccessException();
    }
  }

}
