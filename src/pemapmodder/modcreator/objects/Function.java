package pemapmodder.modcreator.objects;

import java.util.Map;

public class Function{
	private String namespace, name, returnType;
	private Map<String, String> args;
	public Function(String namespace, String name, Map<String, String> args, String returnType){
		this.namespace = namespace;
		this.name = name;
		this.returnType = returnType;
		this.args = args;
	}
	public String getNamespace(){
		return namespace;
	}
	public String getName(){
		return name;
	}
	public String getReturnType(){
		return returnType;
	}
	public Map<String, String> getArgs(){
		return args;
	}
	public String getQualifiedName(){
		return namespace.concat(name);
	}
}
