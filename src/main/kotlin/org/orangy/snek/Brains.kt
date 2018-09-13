package org.orangy.snek

val brain1 = snekBrain(7, 7) {
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

val brain2 = snekBrain(7, 7) {
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

val brain3 = snekBrain(7, 7) {
    pattern("""|
            |    
            |    
            |   t
            |   H
            |    
            |    
            """.trimMargin("|"))
}
