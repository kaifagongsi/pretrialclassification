package com.kfgs.pretrialclassification.dao;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;

@Repository
public interface FenleiBaohuIPCMapper {
    HashSet<String> getHashSetFromIPCList(ArrayList<String> list);
}
