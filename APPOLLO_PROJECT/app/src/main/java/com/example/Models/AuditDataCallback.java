package com.example.Models;

import java.util.List;

public interface AuditDataCallback {
    void onDataReceived(List<AuditLastDatasListResponse> resultList, int totalAssets, int scannedAssets);
}

