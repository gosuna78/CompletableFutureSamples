/*
 * Copyright (c) 2018.
 */
package com.dell.enterprise.common.samples;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The type Completable sample.
 */
public class CompletableSample {


    /**
     * The exceptionally() callback gives you a chance to recover from errors generated from the original Future.
     * You can log the exception here and return a default value.
     */
    private void exceptionallyExceptionHandling() {
        int age = -1;
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (age < 0) {
                throw new IllegalArgumentException("Age cannot be negative");
            }
            if (age > 18) {
                return "Adult";
            } else {
                return "Child";
            }

        }).exceptionally(ex -> {
            System.out.println("Oops! We have an exception - " + ex.getMessage());
            return "Unknown";
        }).thenApply(result -> {
            if (null != result && result.equalsIgnoreCase("Unknown")) {
                return "Process failed";
            }
            return "Process Succeed";
        });
        try {
            System.out.println(future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The API also provides a more generic method - handle() to recover from exceptions.
     * It is called whether or not an exception occurs.
     */
    private void genericExceptionHandling() {
        int age = -1;
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (age < 0) {
                throw new IllegalArgumentException("Age can not be negative");
            }
            if (age > 18) {
                return "Adult";
            } else {
                return "Child";
            }
        }).handle((res, ex) -> {
            if (ex != null) {
                System.out.println("Oops! We have an exception - " + ex.getMessage());
                return "Unknown!";
            }
            return res;
        });
        try {
            System.out.println(future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param location
     * @return
     */
    CompletableFuture<String> downloadStatus(String location) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Start Download from location: " + location);
                Thread.sleep(2000);
                throw new RuntimeException("Remote failure");
                // System.out.println("End Download from location: " + location);
            } catch (InterruptedException iex) {
                iex.printStackTrace();
            }
            return "Completed --> " + location;
        }).handle((res, ex) -> {
            if (ex != null) {
                System.out.println("Oops! We have an exception - " + ex.getMessage());
                return "Failed --> " + res;
            }
            return res;
        });
    }

    /**
     *
     */
    private void multipleFutures() {
        // List of download locations
        List<String> downloadLocations = Arrays.asList("ftp://127.0.0.1/test1.out",
                "ftp://127.0.0.1/test2.out",
                "ftp://127.0.0.1/test3.out",
                "ftp://127.0.0.1/test4.out",
                "ftp://127.0.0.1/test5.out",
                "ftp://127.0.0.1/test6.out",
                "ftp://127.0.0.1/test7.out",
                "ftp://127.0.0.1/test8.out",
                "ftp://127.0.0.1/test9.out",
                "ftp://127.0.0.1/test10.out"
        );

        // Start download from all locations
        List<CompletableFuture<String>> downloadContents = downloadLocations.stream()
                .map(downloadLocation -> downloadStatus(downloadLocation))
                .collect(Collectors.toList());

        // Combine all futures. 'alOff()' will not output the results
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                downloadContents.toArray(new CompletableFuture[downloadLocations.size()])
        );

        // Use future.join() to get the results
        CompletableFuture<List<String>> allDownloadContentsFuture = allFutures.thenApply( v ->{
            return downloadContents.stream()
                    .map(downloadContentFuture -> downloadContentFuture.join())
                    .collect(Collectors.toList());
        });

        try {
            System.out.println(allDownloadContentsFuture.get().toString().replaceAll(",", "\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Instantiates a new Completable sample.
     */
    CompletableSample() {
        // genericExceptionHandling();
        // exceptionallyExceptionHandling();
        multipleFutures();


    }


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new CompletableSample();
    }
}
