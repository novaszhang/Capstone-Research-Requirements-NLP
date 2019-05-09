# -*- coding: utf-8 -*-
"""
Created on Tue May  7 10:24:04 2019

@author: Nova
"""
import sklearn
import re
import pandas as pd
import numpy as np
import io
from sklearn.compose import ColumnTransformer
from sklearn.impute import SimpleImputer
from sklearn.model_selection import train_test_split
import random
from sklearn.pipeline import Pipeline

from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import OneHotEncoder
from sklearn.preprocessing import StandardScaler

# preliminary experiments
from sklearn.multiclass import OneVsRestClassifier, OneVsOneClassifier
from sklearn.linear_model import SGDClassifier
from sklearn.metrics import precision_score, recall_score, f1_score, accuracy_score, confusion_matrix, roc_auc_score
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier, ExtraTreesClassifier

# visualization
import matplotlib.pyplot as plt
import seaborn as sns

def getDataframe(filename):
    dataframe = pd.read_csv(filename, low_memory=False)
    dataframe.drop(dataframe.tail(25).index,inplace=True)
    print(dataframe.tail(3))
    print(dataframe.shape[0])

    dropList = []
    for i in range(dataframe.shape[0]):
        if str(dataframe['Requirement'][i]) == 'nan':
            dropList.insert(0,i)

    print(dropList)
    for x in range(len(dropList)):
        dataframe = dataframe.drop(dataframe.index[dropList[x]])

    print(dataframe.shape[0])

    dataframe = dataframe.reset_index()
    return dataframe

def createReqFiles(filename):
    dataframe = getDataframe(filename)
    for n in range(dataframe.shape[0]):
        currReq = dataframe['Requirement'][n].replace('"','')

        with io.open("requirements/req"+str(n)+".txt","w", encoding='utf8') as f:
            f.write(currReq)

def match(filename, labelDir):
    df = getDataframe(filename)
    for i in range(df.shape[0]):
# with io.open("token_requirements/token"+str(i)+".txt","r", encoding='utf8') as f:
        with io.open("token_requirements/token"+str(i)+".txt","r", encoding='utf8') as f:
           lineList = f.readlines()

        labelFile = io.open("labeled_requirements/labeled"+str(i)+".tsv","w", encoding='utf8')
        for el in lineList:
            category = ''
            el = el[:-1]
            countCategories = 0
            if el in str(df['Spatial'][i]):
                category = 'SPATIAL'
                countCategories +=1
            if el in str(df['Temporal'][i]):
                category = 'TEMPORAL'
                countCategories +=1
            if el in str(df['Compare'][i]):
                category = 'COMP'
                countCategories +=1
            if el in str(df['Conditional'][i]):
                category = 'COND'
                countCategories +=1
            if el in str(df['Probability'][i]):
                category = 'PROB'
                countCategories +=1
            if el in str(df['Aggregation'][i]):
                category = 'AGGR'
                countCategories +=1

            if countCategories > 1 or countCategories == 0:
                category = '0'

            labelFile.write(el+"\t"+category+"\n")
        labelFile.close()

def combineFiles(trainNum):
    allList = [x for x in range(163)]
    random.shuffle(allList)

    trainList = allList[0:trainNum]
    testList=allList[trainNum:163]

    trainFile = io.open("train.tsv","a+",encoding = 'utf-8')
    for i in trainList:
         with io.open("labeled_requirements/labeled"+str(i)+".tsv","r", encoding='utf8') as f:
           for el in f.readlines():
               trainFile.write(el)
    trainFile.close()
    testFile = io.open("test.tsv","a+",encoding = 'utf-8')
    testFull = io.open("testFull.txt","a+", encoding='utf-8')
    for i in testList:
        with io.open("labeled_requirements/labeled"+str(i)+".tsv","r", encoding='utf8') as f:
            for el in f.readlines():
                testFile.write(el)
        with io.open("requirements/req"+str(i)+".txt","r", encoding = 'utf8') as f1:
            for el in f1.readlines():
                testFull.write(el)
    testFile.close()
    testFull.close()

if __name__=="__main__":
   # createReqFiles("CityRequire.csv")
    #after getting tokens:
    combineFiles(100)