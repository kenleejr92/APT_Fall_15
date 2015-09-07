__author__ = 'kenlee'

class student :
    def __init__(self,name,gpa,age):
        self.name = name
        self.gpa = gpa
        self.age = age
    def __str__(self):
        return str(self.name) + " " + str(self.gpa) + " " + str(self.age)
    def __lt__(self, other):
        if self.gpa < other.gpa :
            return True
        elif self.gpa == other.gpa :
            if self.name < other.name :
                return True
            elif self.name__eq__(other.name) :
                if self.age < other.age :
                    return True
                else :
                    return False
            else :
                return False
        else :
            return False
    def __eq__(self, other):
        if (self.name.__eq__(other.name) and self.gpa__eq__(other.gpa) and self.age.__eq__(self.age)) :
            return True
        else :
            return False
    def __hash__(self) :
        return self.name.__hash__()

s = [student("Ken",3.7,22),student("Ken",3.7,44),student("Ken",3.7,32),student("Ken",3.7,19),student("Ken",3.7,20)]
s.sort(lambda x,y : cmp(x.gpa,y.gpa) if x.gpa != y.gpa else cmp(x.name,y.name) if x.name != y.name else cmp(x.age,y.age))

l = list()
for i in s :
    l.append((i.name,i))
d = dict(l)
y = d.values()
print '\n'.join(map(str,s))