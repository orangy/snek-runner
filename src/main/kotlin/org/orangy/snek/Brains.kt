package org.orangy.snek

val brain1 = snekBrain {
    pattern("""|
            |    
            |    
            |   t
            |   H
            |    
            |    
            """.trimMargin("|"))
    pattern("""|
            |    
            |    
            |   Th
            |   H
            |    
            |    
            """.trimMargin("|"))
    pattern("""|
            |    
            |   h
            |   T
            |   H 
            |    
            |    
            """.trimMargin("|"))
    pattern("""|
            |    
            |  .
            | ..
            |  H    
            | 
            | 
            """.trimMargin("|"))
}

val brain2 = snekBrain {
    pattern("""|
            |    
            |    
            |   t
            |   H
            |    
            |    
            """.trimMargin("|"))
    pattern("""|
            |    
            |   t
            |   .
            |   H
            |    
            |    
            """.trimMargin("|"))
    pattern("""|
            |    
            |   
            |   .t
            |   H 
            |    
            |    
            """.trimMargin("|"))
    pattern("""|
            |    
            |  .
            | ..
            |  H    
            | 
            | 
            """.trimMargin("|"))
}

val brain3 = snekBrain {
    pattern("""|
            |    
            |    
            |   t
            |   H
            |    
            |    
            """.trimMargin("|"))
}
