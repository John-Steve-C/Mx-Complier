#include<bits/stdc++.h>
using namespace std;
void f_print_1(char* str_1){
	printf("%s",str_1);
}
void f_println_1(char* str_2){
	printf("%s\n",str_2);
}
void f_printInt_1(int n_1){
	printf("%d",n_1);
}
void f_printlnInt_1(int n_2){
	printf("%d\n",n_2);
}
char* f_getString_1(){
	string str;
	cin>>str;
	char *str2=new char[strlen(str.c_str())+1];
	strcpy(str2,str.c_str());
	char *p=&str2[0];
	return p;
}
int f_getInt_1(){
	int a;
	scanf("%d",&a);
	return a;
}
char* f_toString_1(int i_1){
	string str;
	int flag=0;
	if(i_1==0){
		char *str2=new char[2];
		str2[0]='0',str2[1]='\0';
		return &str2[0];
	}
	if(i_1<0)flag=1,i_1=-i_1;
	while(i_1){
		str+=(char)(i_1%10+'0');
		i_1/=10;
	}
	if(flag)str+=char('-');
	char *str2=new char[strlen(str.c_str())+1];
	strcpy(str2,str.c_str());
	int size=strlen(str2);
	for(int i=(size-1)/2;i>=0;i--)swap(str2[i],str2[size-i-1]);
	return &str2[0];
}
bool _stringcmp_eq_1(char* str1_1,char* str2_1){
	return strcmp(str1_1,str2_1)==0;
}
bool _stringcmp_neq_1(char* str1_2,char* str2_2){
	return strcmp(str1_2,str2_2)!=0;
}
bool _stringcmp_less_1(char* str1_3,char* str2_3){
	return strcmp(str1_3,str2_3)<0;
}
bool _stringcmp_greater_1(char* str1_4,char* str2_4){
	return strcmp(str1_4,str2_4)>0;
}
bool _stringcmp_leq_1(char* str1_5,char* str2_5){
	return strcmp(str1_5,str2_5)<=0;
}
bool _stringcmp_geq_1(char* str1_6,char* str2_6){
	return strcmp(str1_6,str2_6)>=0;
}
int _string_length_1(char* str_3){
	int x=0;
	while(str_3[x]!='\0')x++;
	return x;
}
char* _string_merge_1(char* str1_7,char* str2_7){
	int len1=_string_length_1(str1_7),len2=_string_length_1(str2_7);
	char* str=new char[len1+len2+1];
	for(int i=0;i<len1;i++)str[i]=str1_7[i];
	for(int i=0;i<len2;i++)str[len1+i]=str2_7[i];
	str[len1+len2]='\0';
	return str;
}
char* _heap_malloc_1(int n_3){
	return (char*)malloc(n_3);
}
char* _string_substring_1(char* str_4,int l_1,int r_1){
	char* str=new char[r_1-l_1];
	for(int i=l_1;i<r_1;i++)str[i-l_1]=str_4[i];
	return str;
}
int _string_parseInt_1(char* str_5){
	int ans=0,i=0;
	while(str_5[i]!='\0'){
		ans=ans*10+str_5[i]-'0';
		i++;
	}
	return ans;
}
int _string_ord_1(char* str_6,int ord_1){
	return (int)str_6[ord_1];
}