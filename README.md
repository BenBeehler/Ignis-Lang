# Ignis-Lang
A simple event driven procedural scalable language built on top of the JVM.
# Basic Calculator Example
<br>
```ruby

ref module Main
	ref fn auto Main : void
		string operator = IO.Read
		string oneStr = IO.Read
		string twoStr = IO.Read
		
		decimal one = ToDecimal oneStr
		decimal two = ToDecimal twoStr
		
		bool add? = Equals operator, "+"
		bool sub? = Equals operator, "-"
		bool mult? = Equals operator, "*"
		bool div? = Equals operator, "/"
		
		ref if add?
			IO.Println one+two
		end
		
		ref if sub?
			IO.Println one-two
		end
		
		ref if mult?
			IO.Println one*two
		end
		
		ref if div?
			IO.Println one/two
		end
	end
end
```
