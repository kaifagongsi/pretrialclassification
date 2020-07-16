package com.kfgs.pretrialclassification.dao;

import java.util.ArrayList;
import java.util.HashSet;

public interface FenleiBaohuIPCMapper {
    HashSet<String> getHashSetFromIPCList(ArrayList<String> list);
}
