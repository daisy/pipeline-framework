from optparse import OptionParser
from lxml import etree

"""
find all places where XYZ is a potential direct child
"""
RNG_NS = "{http://relaxng.org/ns/structure/1.0}"

def find_all_defines(name_value, document):
    return _find_all_defines(name_value, document, [])

def _find_all_defines(name_value, document, l):
    xpath_expr = "//%(rng)sdefine[descendant::%(rng)sref[@name='%(name)s']]" % {"rng": RNG_NS, "name": name_value}
        
    xpath_fn = etree.ETXPath(xpath_expr)
    results = xpath_fn(document.getroot())
    
    for r in results:
        if r.getchildren()[0].tag != "%selement" % RNG_NS:
            if r not in l:
                l.append(r)
                _find_all_defines(r.attrib['name'], document, l)
    return l

def find_elements_with_refs(name_value, document):
    xpath_expr = "//%(rng)selement[descendant::%(rng)sref[@name='%(name)s']]" % {"rng": RNG_NS, "name": name_value}
    xpath_fn = etree.ETXPath(xpath_expr)
    results = xpath_fn(document.getroot())
    
    return results

def main():
    usage = """usage: %prog element rng"""
    description = "Finds all potential parents of a given element"
    
    parser = OptionParser(usage=usage, description=description)
    opts, args = parser.parse_args()
    
    if len(args) != 2:
        print "Wrong number of arguments."
        print usage
        exit(1)
    
    elmname = args[0]
    rngfile = args[1]
    xmldoc = etree.parse(rngfile)
    defines = find_all_defines(elmname, xmldoc)
    for d in defines: 
        #print "(%s)" % d.attrib['name']
        
        elms = find_elements_with_refs(d.attrib['name'], xmldoc)
        for e in elms:
            print e.attrib['name']
    
    
    

if __name__ == "__main__": main()
    