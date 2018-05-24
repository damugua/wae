package cn.finder.wae.api.sdk.request;import cn.finder.httpcommons.request.SearchRequest;import cn.finder.wae.api.sdk.response.FindUserInfoResponse;/**** 获取用户信息接口请求*/public class FindUserInfoRequest extends SearchRequest<FindUserInfoResponse>{	@Override	public  String apiName()	{		return	"wae.user.get";	}	@Override	public  void validate()	{	}	private	java.util.Date	birthday;	public java.util.Date getBirthday()	{		 return birthday;	}	public void setBirthday(java.util.Date birthday)	{		 this.birthday=birthday;	}	private	int	id;	public int getId()	{		 return id;	}	public void setId(int id)	{		 this.id=id;	}	private	String	name;	public String getName()	{		 return name;	}	public void setName(String name)	{		 this.name=name;	}	private	int	age;	public int getAge()	{		 return age;	}	public void setAge(int age)	{		 this.age=age;	}	private	String	account;	public String getAccount()	{		 return account;	}	public void setAccount(String account)	{		 this.account=account;	}	private	String	department_name;	public String getDepartment_name()	{		 return department_name;	}	public void setDepartment_name(String department_name)	{		 this.department_name=department_name;	}	private	String	role_name;	public String getRole_name()	{		 return role_name;	}	public void setRole_name(String role_name)	{		 this.role_name=role_name;	}}