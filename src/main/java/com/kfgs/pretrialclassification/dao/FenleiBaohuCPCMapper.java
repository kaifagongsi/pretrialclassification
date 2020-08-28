package com.kfgs.pretrialclassification.dao;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;

@Repository
public interface FenleiBaohuCPCMapper {
    HashSet<String> getHashSetFromCPCList(ArrayList<String> list);
}
