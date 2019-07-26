pre{

}

rule Model2Model 
	match l : Left!Model 
	with r : Right!Model {
	
	compare : true
}

rule Package2Package 
	match l : Left!Package 
	with r : Right!Package {
	
	compare : true
}

rule DataType2DataType
	match l : Left!DataType 
	with r : Right!DataType {

	compare :
		l.name = r.name and 
		l.isAbstract = r.isAbstract and
		l.powertypeExtent = r.powertypeExtent and
		l.Package.matches(r.Package)
}

rule PrimitiveType2PrimitiveType
	match l : Left!PrimitiveType 
	with r : Right!PrimitiveType {

	compare :
		l.name = r.name and 
		l.isAbstract = r.isAbstract and
		l.powertypeExtent = r.powertypeExtent and
		l.Package.matches(r.Package)
}

rule Enumeration2Enumeration
	match l : Left!Enumeration 
	with r : Right!Enumeration {

	compare :
		l.name = r.name and 
		l.isAbstract = r.isAbstract and
		l.Package.matches(r.Package)
}

rule EnumerationLiteral2EnumerationLiteral
	match l : Left!EnumerationLiteral 
	with r : Right!EnumerationLiteral {

	compare :
		l.name = r.name and 
		l.Visibility = r.Visibility and
	//	l.Package.matches(r.Package)
		l.Namespace.matches(r.Namespace)
}

rule Class2Class
	match l : Left!Class 
	with r : Right!Class {

	compare :
		l.name = r.name and 
		l.isAbstract = r.isAbstract and
		l.Visibility = r.Visibility and
		l.Package.matches(r.Package)
}

rule Class2Interface
	match l : Left!Class 
	with r : Right!Interface {

	compare :
		l.name = r.name and 
		l.isAbstract = r.isAbstract and
		l.Visibility = r.Visibility and
		l.Package.matches(r.Package)
}

rule Interface2Class
	match l : Left!Interface 
	with r : Right!Class {

	compare :
		l.name = r.name and 
		l.isAbstract = r.isAbstract and
		l.Visibility = r.Visibility and
		l.Package.matches(r.Package)
}

rule Interface2Interface
	match l : Left!Interface 
	with r : Right!Interface {

	compare :
		l.name = r.name and 
		l.isAbstract = r.isAbstract and
		l.Visibility = r.Visibility and
		l.Package.matches(r.Package)
}

rule Dependency2Dependency
	match l : Left!Dependency 
	with r : Right!Dependency {

	compare :
		l.name = r.name and 
		l.visibility = r.visibility and 
		l.clients.matches(r.clients) and
		l.Suppliers.matches(r.Suppliers)
}


rule AssociationClass2AssociationClass
	match l : Left!AssociationClass 
	with r : Right!AssociationClass {

	compare :
		l.name = r.name and 
		l.isAbstract = r.isAbstract and
		l.Package.matches(r.Package) and 
		l.memberEnd.size = r.memberEnd.size and
		l.memberEnd.matches(r.memberEnd)
	//	haveEqualPropertyType(l,r)
}

operation haveEqualPropertyType (l : Left!Association, r : Right!Association) : Boolean{ 
	for(m in l.memberEnd)
	{
		var flag = true ; 
		for(n in r.memberEnd)
		{
			if(m.Type.name == n.Type.name)
			{
				flag = false ;
				break ;
			}
		}
		if(flag == true)
			return false ; 
	}
	return true ; 
}

rule Association2Association
	match l : Left!Association 
	with r : Right!Association {

	compare :
		l.name = r.name and 
		l.isAbstract = r.isAbstract and
		l.Package.matches(r.Package) and 
		l.memberEnd.size = r.memberEnd.size //and
	//	haveEqualPropertyType(l,r) /////////////////////////////////////////
		//l.memberEnd.matches(r.memberEnd)		
		//haveEqualProperty(l)
}

/*
operation haveEqualProperty (l : Left!Association) : Boolean{ 
		for(LAP in Left!Property) 
			if(LAP.Association.isDefined() and LAP.Association.name == l.name)
			{	 
				var flag = false ;
				for(RAP in Right!Property)
					if(RAP.Association.isDefined() and RAP.Association.name == LAP.Association.name
					 and RAP.name == LAP.name)
					{
						flag = true ;
					}
				if(flag)
					flag = false ; 
				else
					return false ;
			}
		
		return true ; 
}
*/

/*rule Pro2Pro
	match l : Left!Property
	with r : Right!Property{
		
	compare:
	//c|c.general.name == r.Type and c.owner == l.Type
		Right!Generalization.all.exists(c|c.general.name == r.Type.name and c.owner.name == l.Type.name)
		
		
		do{
			"......".println() ; 
			//var p = Right!Generalization.all.selectOne(c|c.general == r.Type and c.owner == l.Type) ;
			var p = Right!Generalization.all.selectOne(c|c.general.name == "Person" and c.owner.name == "Employee") ;
			
			if(p.isDefined())
			p.general.println() ; 
			r.Type.name.println() ; 
			if(p.isDefined())
			p.owner.println() ; 
			l.Type.name.println() ; 
			"*******".println() ;
		
		}
}*/


rule Property2Property
	match l : Left!Property 
	with r : Right!Property {

	compare :
		(
			l.Namespace.matches(r.Namespace) 
			and Right!Generalization.all.exists(c|c.general.isDefined() and 
				r.Type.isDefined() and c.general.name == r.Type.name and
				l.Type.isDefined() and c.owner.name == l.Type.name)
		)
		or
		(
			l.name = r.name and 
			l.Aggregation = r.Aggregation and
			l.Namespace.matches(r.Namespace) and 
			l.Association.matches(r.Association)
		)
}

rule LiteralInt2LiteralInt
	match l : Left!LiteralInteger
	with r : Right!LiteralInteger {
	
	compare : 
		l.value = l.value  and
		l.eContainer.matches(r.eContainer)
}

rule LiteralUnlimitedNatural2LiteralUnlimitedNatural
	match l : Left!LiteralUnlimitedNatural
	with r : Right!LiteralUnlimitedNatural {
	
	compare : 
		l.value = l.value  and
		l.eContainer.matches(r.eContainer)
}

	
rule Operation2Operation
	match l : Left!Operation 
	with r : Right!Operation {

	compare :
		l.name = r.name and 
		l.isAbstract = r.isAbstract and
		l.isStatic = r.isStatic and
		l.isQuery = r.isQuery and
		l.Visibility = r.Visibility and		
		l.Namespace.matches(r.Namespace) and 
		l.Concurrency = r.Concurrency and
		l.method.matches(r.method) and
		l.bodyCondition = r.bodyCondition and
		l.postcondition.matches(r.postcondition) and
		l.precondition.matches(r.precondition)		
}

rule Constraint2Constraint
	match l : Left!Constraint
	with r : Right!Constraint{
	
	compare: 
		l.name = r.name and
		l.constrainedElement.matches(r.constrainedElement) and
		l.Namespace.matches(r.Namespace)
}

rule Generalization2Generalization
	match l : Left!Generalization
	with r : Right!Generalization {

	compare : 
	//	l.general.name = r.general.name and
	//	l.general.type.matches(r.general.type) and
		l.general.matches(r.general) and
		l.eContainer.matches(r.eContainer)
	//	l.eContainer.name = r.eContainer.name
}
/*
post{
	var count : Integer = 0;
	"MatchTrace size is:".print() ; 
	var size : Integer = matchTrace.matches.size().println();
	while(count < size){
		var current = matchTrace.matches[count];
		if(current.matching){
			current.left.eClass.name.print() ; 
			' : '.print();
			
			if(current.left.eClass.name == "Generalization"){
				current.left.eContainer.name.print(); " of ".print() ;
				current.left.general.name.print();  
				' <--> '.print();
				current.right.eContainer.name.print(); " of ".print() ;
				current.right.general.name.print();  
			}
			else{
				current.left.name.print();
				' <--> '.print();
				current.right.name.print();
			}
			"".println() ; 
		}	
		count = count + 1;
	}
}
*/